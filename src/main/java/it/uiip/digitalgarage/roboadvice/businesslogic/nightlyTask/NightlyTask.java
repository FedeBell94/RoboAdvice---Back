package it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask;

import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater.IDataUpdater;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dateProvider.DateProvider;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dateProvider.LiarDateProvider;
import it.uiip.digitalgarage.roboadvice.persistence.model.*;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.PortfolioRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.StrategyRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: multithreaded??

@Service
public class NightlyTask implements INightlyTask {

    private static final Log LOGGER = LogFactory.getLog(NightlyTask.class);

    private final StrategyRepository strategyRepository;
    private final PortfolioRepository portfolioRepository;
    private final AssetRepository assetRepository;
    private final DataRepository dataRepository;

    private final IDataUpdater dataUpdater;
   // private final IDataSource dataSource;

    @Autowired
    public NightlyTask(final StrategyRepository strategyRepository, final PortfolioRepository portfolioRepository,
                       final AssetRepository assetRepository, final DataRepository dataRepository,
                       final IDataUpdater dataUpdater) {
        this.strategyRepository = strategyRepository;
        this.portfolioRepository = portfolioRepository;
        this.assetRepository = assetRepository;
        this.dataRepository = dataRepository;
        this.dataUpdater = dataUpdater;
    }


    /**
     * The default start worth of a user. It is set to 10000.
     */
    private static final BigDecimal DEFAULT_START_WORTH = new BigDecimal(10000);

    @Override
    public void executeNightlyTask(final DateProvider dateProvider, final Iterable<User> users) {

        // #1: update data (only if not in demo mode)
        if (!(dateProvider instanceof LiarDateProvider)) {
            //dataUpdater.updateDailyData();
            dataConsistency();
        }

        // Finds all assets
        final Iterable<Asset> assets = assetRepository.findAll();

        // For each asset finds the latest price
        final Map<Long, BigDecimal> latestPrices = findAssetsPrice(assets, dateProvider);


        // Finds the assets changed today
        final List<Data> todayNewPrices = dataRepository.findByDate(dateProvider.getYesterday());

        for (User currUser : users) {
            // Find the portfolio of yesterday for the current user
            List<Portfolio> userPortfolio =
                    portfolioRepository.findByUserAndDate(currUser, dateProvider.getYesterday());

            // Case of brand new user
            if (userPortfolio.isEmpty()) {
                // #2: create portfolio for fresh(new) users
                createPortfolio(dateProvider, currUser, assets, DEFAULT_START_WORTH, latestPrices);
            } else {

                // Finds the active strategy of the current user
                List<Strategy> userStrategy = strategyRepository.findByUserAndActiveTrue(currUser);

                // Check if the user strategy was changed yesterday
                if (userStrategy.get(0).getStartingDate().getTime() == dateProvider.getYesterday().getTime()) {

                    // #4: compute portfolio for 'old' users which has changed the strategy (same as #2)
                    BigDecimal userWorth = computeWorth(userPortfolio, latestPrices);
                    createPortfolio(dateProvider, currUser, assets, userWorth, latestPrices);
                } else {
                    // #3: update portfolio for 'old' users which didn't change the strategy yesterday
                    updatePortfolio(dateProvider, userPortfolio, todayNewPrices);

                }
            }
        }
    }

    private BigDecimal computeWorth(final List<Portfolio> portfolios, final Map<Long, BigDecimal> latestPrices) {
        BigDecimal worth = new BigDecimal(0);
        for (Portfolio currPortfolio : portfolios) {
            BigDecimal singleWorth =
                    currPortfolio.getUnit().multiply(latestPrices.get(currPortfolio.getAsset().getId()));
            worth = worth.add(singleWorth);
        }
        return worth;
    }

    private void updatePortfolio(final DateProvider dateProvider, final List<Portfolio> portfolios,
                                 final List<Data> todayNewPrices) {

        final Date currDate = dateProvider.getToday();


        List<Portfolio> insertList = new ArrayList<>();
        for (Portfolio currPortfolio : portfolios) {

            Data latestAssetPrice = null;
            for (Data currData : todayNewPrices) {
                if (currData.getAsset().getId() == currPortfolio.getAsset().getId()) {
                    latestAssetPrice = currData;
                }
            }

            if (latestAssetPrice != null) {
                BigDecimal assetMoney = currPortfolio.getUnit().multiply(latestAssetPrice.getValue());

                insertList.add(Portfolio.builder()
                        .user(currPortfolio.getUser())
                        .assetClass(currPortfolio.getAssetClass())
                        .asset(currPortfolio.getAsset())
                        .unit(currPortfolio.getUnit())
                        .value(assetMoney)
                        .date(currDate)
                        .build());
            } else {
                insertList.add(Portfolio.builder()
                        .user(currPortfolio.getUser())
                        .assetClass(currPortfolio.getAssetClass())
                        .asset(currPortfolio.getAsset())
                        .unit(currPortfolio.getUnit())
                        .value(currPortfolio.getValue())
                        .date(currDate)
                        .build());
            }
        }
        portfolioRepository.save(insertList);
    }


    private Map<Long, BigDecimal> findAssetsPrice(final Iterable<Asset> assets, final DateProvider dateProvider) {
        Map<Long, BigDecimal> latestPrices = new HashMap<>();
        for (Asset curr : assets) {
            Data data = dataRepository.findTop1ByDateBeforeAndAssetOrderByDateDesc(dateProvider.getToday(), curr);
            latestPrices.put(data.getAsset().getId(), data.getValue());
        }
        return latestPrices;
    }


    private void createPortfolio(final DateProvider dateProvider, final User user, final Iterable<Asset> assets,
                                 final BigDecimal totalMoney, final Map<Long, BigDecimal> latestPrices) {

        final Date currDate = dateProvider.getToday();
        // Finds the active strategy of the current user
        List<Strategy> userStrategy = strategyRepository.findByUserAndActiveTrue(user);

        List<Portfolio> insertList = new ArrayList<>();
        for (Strategy currStrategy : userStrategy) {
            for (Asset currAsset : assets) {
                if (currAsset.getAssetClass().equals(currStrategy.getAssetClass())) {

                    /*
                     * assetMoney = totalMoney*(assetClassStrategyPerc)*(assetDistributionPerc)
                     *
                     * assetMoney = totalMoney*(assetClassStrategy / 100)*(assetDistribution / 100)
                     *
                     * assetMoney = totalMoney*(assetClassStrategy * assetDistribution)/10000
                     */
                    BigDecimal assetMoney = totalMoney.multiply(currStrategy.getPercentage())
                            .multiply(currAsset.getFixedPercentage()).divide(new BigDecimal(10000), 4);

                    BigDecimal latestAssetPrice = latestPrices.get(currAsset.getId());
                    BigDecimal assetUnits = assetMoney.divide(latestAssetPrice, 4, RoundingMode.HALF_UP);

                    //LOGGER.debug(assetMoney + " " + latestAssetPrice + " " + assetUnits);

                    insertList.add(Portfolio.builder()
                            .user(user)
                            .assetClass(currAsset.getAssetClass())
                            .asset(currAsset)
                            .unit(assetUnits)
                            .value(assetMoney)
                            .date(currDate)
                            .build());
                }
            }
        }
        portfolioRepository.save(insertList);
    }

    public void dataConsistency() {

//        Iterable<Asset> assets = assetRepository.findAll();
//        DateProvider dateProvider = new DateProvider();
//        LocalDate lastDate = dateProvider.getYesterday().toLocalDate();
//
//        for (Asset currAsset : assets) {
//            Data data = dataRepository.findTop1ByAssetOrderByDateDesc(currAsset);
//
//            dataSource.getHistoricalData(currAsset, data.getDate().toLocalDate().getYear(),
//                    data.getDate().toLocalDate().getMonthValue(), data.getDate().toLocalDate().getDayOfMonth());
//
//        }
    }

    /*@PostConstruct
    public void func(){
        dataConsistency();
    }*/
}

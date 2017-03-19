package it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask;

import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater.IDataUpdater;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dateProvider.LiarDateProvider;
import it.uiip.digitalgarage.roboadvice.persistence.model.*;
import it.uiip.digitalgarage.roboadvice.persistence.repository.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NightlyTask implements INightlyTask {

    private static final Log LOGGER = LogFactory.getLog(NightlyTask.class);

    private final StrategyRepository strategyRepository;
    private final PortfolioRepository portfolioRepository;
    private final AssetRepository assetRepository;
    private final DataRepository dataRepository;
    private final UserRepository userRepository;
    private final IDataUpdater dataUpdater;

    @Autowired
    public NightlyTask(final StrategyRepository strategyRepository, final PortfolioRepository portfolioRepository,
                       final AssetRepository assetRepository, final DataRepository dataRepository,
                       final UserRepository userRepository, final IDataUpdater dataUpdater) {
        this.strategyRepository = strategyRepository;
        this.portfolioRepository = portfolioRepository;
        this.assetRepository = assetRepository;
        this.dataRepository = dataRepository;
        this.userRepository = userRepository;
        this.dataUpdater = dataUpdater;
    }

    // The default start worth of a user. It is set to 10000.
    private static final BigDecimal DEFAULT_START_WORTH = new BigDecimal(10000);


    @Override
    public void executeNightlyTask(final Iterable<User> users) {

        // #1: update assets data
        try {
            dataUpdater.updateAssetData();
        } catch(IDataUpdater.DataUpdateException e){
            LOGGER.error("Failed to execute nightly task due to an error in updating asset data.");
            return;
        }

        // Finds all assets
        final Iterable<Asset> assets = assetRepository.findAll();

        // Compute the daily portfolio for each user
        for (User currUser : users) {

            // If the user is new creates the portfolio
            if (currUser.getLastPortfolioComputation() == null) {

                // For each asset finds the latest price for the registration's day
                final Map<Long, BigDecimal> latestPrices = findAssetsPriceDay(assets, currUser.getRegistration());

                // TODO remove top 4 hardcoding, checking the last portfolio date
                List<Strategy> userStrategy = strategyRepository
                        .findTop4ByUserAndStartingDateLessThanEqualOrderByStartingDateDesc(currUser,
                                currUser.getRegistration());
                if(userStrategy.isEmpty()){
                    // If the strategy is not set i do not compute the portfolio for this user
                    continue;
                }
                Date tomorrow = new Date(currUser.getRegistration().getTime() + 24 * 60 * 60 * 1000);
                createPortfolio(currUser, tomorrow, assets, DEFAULT_START_WORTH, latestPrices, userStrategy);
                currUser.setLastPortfolioComputation(tomorrow);
            }

            LiarDateProvider dateProvider = new LiarDateProvider(currUser.getLastPortfolioComputation().toString());
            dateProvider.goNextDay();
            // Computes all the portfolios for the user in case the nightly task failed in the previous days
            while (dateProvider.getToday().compareTo(Date.valueOf(LocalDate.now())) <= 0) {

                // Find last portfolio computed for the current user
                List<Portfolio> userPortfolio =
                        portfolioRepository.findByUserAndDate(currUser, dateProvider.getYesterday());

                // Finds the active strategy of the current user for the current date
                List<Strategy> userStrategy = strategyRepository
                        .findTop4ByUserAndStartingDateLessThanEqualOrderByStartingDateDesc(currUser,
                                dateProvider.getToday());
                if(userStrategy.isEmpty()){
                    // If the strategy is not set i do not compute the portfolio for this user
                    continue;
                }

                // For each asset finds the latest price
                final Map<Long, BigDecimal> latestPrices = findAssetsPriceDay(assets, dateProvider.getToday());

                // Check if the user strategy was changed yesterday
                if (userStrategy.get(0).getStartingDate().compareTo(dateProvider.getYesterday()) == 0) {

                    // #4: compute portfolio for 'old' users which has changed the strategy (same as #2)
                    BigDecimal userWorth = computeWorth(userPortfolio, latestPrices);
                    createPortfolio(currUser, dateProvider.getToday(), assets, userWorth, latestPrices, userStrategy);
                } else {
                    // #3: update portfolio for 'old' users which didn't change the strategy yesterday
                    List<Data> todayNewPrices = dataRepository.findByDate(dateProvider.getToday());
                    updatePortfolio(dateProvider.getToday(), userPortfolio, todayNewPrices);
                }

                dateProvider.goNextDay();
            }

            // Update of last portfolio computation date
            currUser.setLastPortfolioComputation(dateProvider.getYesterday());
            userRepository.save(currUser);
        }
    }

    private void createPortfolio(final User user, final Date date, final Iterable<Asset> assets, final BigDecimal worth,
                                 final Map<Long, BigDecimal> latestPrices, List<Strategy> userStrategy) {

        List<Portfolio> insertList = new ArrayList<>();
        for (Strategy currStrategy : userStrategy) {
            for (Asset currAsset : assets) {
                if (currAsset.getAssetClass().getId().equals(currStrategy.getAssetClass().getId())) {

                    BigDecimal assetMoney = worth.multiply(currStrategy.getPercentage())
                            .multiply(currAsset.getFixedPercentage()).divide(new BigDecimal(10000), 4);

                    BigDecimal latestAssetPrice = latestPrices.get(currAsset.getId());
                    BigDecimal assetUnits = assetMoney.divide(latestAssetPrice, 4, RoundingMode.HALF_UP);

                    insertList.add(Portfolio.builder()
                            .user(user)
                            .assetClass(currAsset.getAssetClass())
                            .asset(currAsset)
                            .unit(assetUnits)
                            .value(assetMoney)
                            .date(date)
                            .build());
                }
            }
        }
        portfolioRepository.save(insertList);
    }

    private Map<Long, BigDecimal> findAssetsPriceDay(final Iterable<Asset> assets, final Date date) {
        Map<Long, BigDecimal> latestPrices = new HashMap<>();
        for (Asset curr : assets) {
            Data data = dataRepository.findTop1ByDateLessThanEqualAndAssetOrderByDateDesc(date, curr);
            latestPrices.put(data.getAsset().getId(), data.getValue());
        }
        return latestPrices;
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

    private void updatePortfolio(final Date date, final List<Portfolio> portfolios,
                                 final List<Data> todayNewPrices) {

        List<Portfolio> insertList = new ArrayList<>();
        for (Portfolio currPortfolio : portfolios) {

            Data latestAssetPrice = null;
            for (Data currData : todayNewPrices) {
                if (currData.getAsset().getId().compareTo(currPortfolio.getAsset().getId()) == 0) {
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
                        .date(date)
                        .build());
            } else {
                insertList.add(Portfolio.builder()
                        .user(currPortfolio.getUser())
                        .assetClass(currPortfolio.getAssetClass())
                        .asset(currPortfolio.getAsset())
                        .unit(currPortfolio.getUnit())
                        .value(currPortfolio.getValue())
                        .date(date)
                        .build());
            }
        }
        portfolioRepository.save(insertList);
    }
}

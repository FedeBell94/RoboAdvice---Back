package it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate;

import it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.dataUpdater.DateProvider.DateProvider;
import it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.dataUpdater.IDataUpdater;
import it.uiip.digitalgarage.roboadvice.persistence.model.*;
import it.uiip.digitalgarage.roboadvice.persistence.repository.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: multithreaded??

@Service
public class DailyTaskUpdate implements IDailyTaskUpdate {


    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private UserRepository userRepository;

    // Autowire with data updater
    @Autowired
    private IDataUpdater dataUpdater;

    private static final Log LOGGER = LogFactory.getLog(DailyTaskUpdate.class);

    /**
     * The default start worth of a user. It is set to 10000.
     */
    private static final BigDecimal DEFAULT_START_WORTH = new BigDecimal(10000);

    @Override
    public void executeUpdateTask(final DateProvider dateProvider) {

        // Dates
        final Date yesterday = dateProvider.getYesterday();

        // #1: update data
        dataUpdater.updateDailyData();

        // Finds all assets
        final List<Asset> assets = assetRepository.findAll();
        // For each asset find the latest price
        final Map<Integer, BigDecimal> latestPrices = findAssetsLatestPrice(assets);

        // Finds all users
        final Iterable<User> users = userRepository.findAll();
        for (User currUser : users) {
            // Find the portfolio of yesterday for the current user
            List<Portfolio> userPortfolio = portfolioRepository.findByUserAndDate(currUser, yesterday);

            // Case of brand new user
            if (userPortfolio.isEmpty()) {
                // #2: create portfolio for fresh(new) users
                createPortfolio(dateProvider, currUser, assets, DEFAULT_START_WORTH, latestPrices);
            } else {

                // Finds the active strategy of the current user
                List<Strategy> userStrategy = strategyRepository.findByUserAndActiveTrue(currUser);

                // Check if the user strategy was changed yesterday
                if (userStrategy.get(0).getStartingDate().toString().equals(yesterday.toString())) {

                    // #4: compute portfolio for 'old' users which has changed the strategy (same as #2)
                    BigDecimal userWorth = computeWorth(userPortfolio, latestPrices);
                    createPortfolio(dateProvider, currUser, assets, userWorth, latestPrices);
                } else {
                    // #3: update portfolio for 'old' users which didn't change the strategy yesterday

                    // Check if the user uses and auto-balancing strategy
                    if (currUser.isAutoBalancing()) {
                        BigDecimal userWorth = computeWorth(userPortfolio, latestPrices);
                        createPortfolio(dateProvider, currUser, assets, userWorth, latestPrices);
                    } else {
                        updatePortfolio(dateProvider, userPortfolio, latestPrices);
                    }
                }
            }
        }
    }

    /**
     * Compute the entire worth of a portfolio.
     *
     * @param portfolios
     *         The portfolio on which compute the total worth.
     *
     * @return The worth of the portfolio passed.
     */
    private BigDecimal computeWorth(final List<Portfolio> portfolios, final Map<Integer, BigDecimal> latestPrices) {
        BigDecimal worth = new BigDecimal(0);
        for (Portfolio currPortfolio : portfolios) {
            BigDecimal singleWorth =
                    currPortfolio.getUnit().multiply(latestPrices.get(currPortfolio.getAsset().getId()));
            worth = worth.add(singleWorth);
        }
        return worth;
    }

    /**
     * Update the portfolio passed with the current data assets passed in lateste price.
     *
     * @param portfolios
     *         The portfolio to update.
     * @param latestPrices
     *         The latest assets prices.
     */
    private void updatePortfolio(final DateProvider dateProvider, final List<Portfolio> portfolios,
                                 final Map<Integer, BigDecimal> latestPrices) {

        final Date currDate = dateProvider.getToday();
        for (Portfolio currPortfolio : portfolios) {

            BigDecimal currAssetPrice = latestPrices.get(currPortfolio.getAsset().getId());
            BigDecimal assetMoney = currPortfolio.getUnit().multiply(currAssetPrice);

            portfolioRepository.save(Portfolio.builder()
                    .user(currPortfolio.getUser())
                    .assetClass(currPortfolio.getAssetClass())
                    .asset(currPortfolio.getAsset())
                    .unit(currPortfolio.getUnit())
                    .value(assetMoney)
                    .date(currDate)
                    .build());
        }
    }

    /**
     * Update the portfolio passed with the simulated data.
     *
     * @param portfolios
     *         The portfolio to update.
     * @param latestPrices
     *         The latest assets prices.
     * @param currDate
     *         The simulation date.
     */
    private void updateSimulatedPortfolio(final Iterable<Portfolio> portfolios, Map<Integer, BigDecimal> latestPrices,
                                          Date currDate) {

        for (Portfolio currPortfolio : portfolios) {

            BigDecimal currAssetPrice = latestPrices.get(currPortfolio.getAsset().getId());
            BigDecimal assetMoney = currPortfolio.getUnit().multiply(currAssetPrice);

            portfolioRepository.save(Portfolio.builder()
                    .user(currPortfolio.getUser())
                    .assetClass(currPortfolio.getAssetClass())
                    .asset(currPortfolio.getAsset())
                    .unit(currPortfolio.getUnit())
                    .value(assetMoney)
                    .date(currDate)
                    .build());
        }
    }


    /**
     * Finds the latest price for each asset. Used to evaluate the first portfolio for a user and when a strategy
     * changes.
     *
     * @param assets
     *         Assets to find.
     *
     * @return A {@link Map} containing the {@link Asset} id for key, and the latest price found into the database as
     * value.
     */
    private Map<Integer, BigDecimal> findAssetsLatestPrice(final List<Asset> assets) {
        Map<Integer, BigDecimal> latestPrices = new HashMap<>();
        for (Asset curr : assets) {
            Data data = dataRepository.findFirst1ByAssetOrderByDateDesc(curr);
            latestPrices.put(data.getAsset().getId(), data.getValue());
        }
        return latestPrices;
    }

    /**
     * Finds the price for each asset at a defined date.
     *
     * @param assets
     *         Assets to find.
     * @param day
     *         The date of interest.
     *
     * @return A {@link Map} containing the {@link Asset} id for key, and the price found in database as value.
     */
    private Map<Integer, BigDecimal> findAssetsPriceByDate(final Iterable<Asset> assets, Calendar day) {

        Map<Integer, BigDecimal> prices = new HashMap<>();
        Date date = new java.sql.Date(day.getTimeInMillis());
        for (Asset curr : assets) {
            Data data = dataRepository.findByAssetAndDate(curr, date);
            if (data != null) {
                prices.put(data.getAsset().getId(), data.getValue());
            } else {
                int count = 0;
                while (data == null) {
                    count++;
                    day.add(Calendar.DATE, -1);
                    date = new java.sql.Date(day.getTimeInMillis());
                    data = dataRepository.findByAssetAndDate(curr, date);
                }
                day.add(Calendar.DATE, +count);
                prices.put(data.getAsset().getId(), data.getValue());
            }
        }
        return prices;
    }


    /**
     * Creates a new portfolio for the user passed for the strategy passed with the amount of money given.
     *
     * @param user
     *         The user owner of the portfolio
     * @param assets
     *         The list of all assets
     * @param totalMoney
     *         Amount of money to divide into the different assets
     * @param latestPrices
     *         Map containing latest prices for each Asset (key = asset_id, value = last price)
     */
    private void createPortfolio(final DateProvider dateProvider, final User user, final List<Asset> assets,
                                 final BigDecimal totalMoney, final Map<Integer, BigDecimal> latestPrices) {

        final Date currDate = dateProvider.getToday();
        // Finds the active strategy of the current user
        List<Strategy> userStrategy = strategyRepository.findByUserAndActiveTrue(user);


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

                    LOGGER.debug(assetMoney + " " + latestAssetPrice + " " + assetUnits);

                    portfolioRepository.save(Portfolio.builder()
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
    }

//    /**
//     * Creates a new portfolio for the user passed for the strategy passed with the amount of money given in the given
//     * date.
//     *
//     * @param user
//     *         The user owner of the portfolio
//     * @param assets
//     *         The list of all assets
//     * @param totalMoney
//     *         Amount of money to divide into the different assets
//     * @param latestPrices
//     *         Map containing latest prices for each Asset (key = asset_id, value = last price)
//     * @param currDate
//     *         The date of the simulated portfolio
//     */
//    private void createPortfolioInDate(final User user, final Iterable<Asset> assets, final BigDecimal totalMoney,
//                                       final Map<Integer, BigDecimal> latestPrices, Date currDate) {
//
//        List<Strategy> userStrategy = strategyRepository.findByUserAndActiveTrue(user);
//
//        for (Strategy currStrategy : userStrategy) {
//            for (Asset currAsset : assets) {
//                if (currAsset.getAssetClass().equals(currStrategy.getAssetClass())) {
//
//                    BigDecimal assetMoney = totalMoney.multiply(currStrategy.getPercentage())
//                            .multiply(currAsset.getFixedPercentage()).divide(new BigDecimal(10000), 4);
//
//                    BigDecimal latestAssetPrice = latestPrices.get(currAsset.getId());
//                    BigDecimal assetUnits = assetMoney.divide(latestAssetPrice, 8, RoundingMode.HALF_DOWN);
//
//                    LOGGER.debug(assetMoney + " " + latestAssetPrice + " " + assetUnits);
//
//                    portfolioRepository.save(Portfolio.builder()
//                            .user(user)
//                            .assetClass(currAsset.getAssetClass())
//                            .asset(currAsset)
//                            .unit(assetUnits)
//                            .value(assetMoney)
//                            .date(currDate)
//                            .build());
//                }
//            }
//        }
//    }
//
//    public void SimulateHistory(int daysOfHistory) {
//
//        final Iterable<Asset> assets = assetRepository.findAll();
//
//        final Iterable<User> users = userRepository.findAll();
//
//        final Date today = Utils.getToday();
//
//        for (User currUser : users) {
//
//            Calendar cal = Calendar.getInstance();
//            cal.add(Calendar.DATE, -daysOfHistory);
//            Date day = new java.sql.Date(cal.getTimeInMillis());
//            int dayCount = 1;
//
//            while (dayCount < daysOfHistory) {
//                System.out.println("curr date: " + day.toLocalDate().toString());
//                final Map<Integer, BigDecimal> prices = findAssetsPriceByDate(assets, cal);
//                cal.add(Calendar.DATE, -1);
//                day = new java.sql.Date(cal.getTimeInMillis());
//
//                List<Portfolio> userPortfolio = portfolioRepository.findFirst13ByUserOrderByDateDesc(currUser);
//
//                if (userPortfolio.isEmpty()) {
//
//                    createPortfolioInDate(currUser, assets, DEFAULT_START_WORTH, prices, day);
//                } else {
//                    updateSimulatedPortfolio(userPortfolio, prices, day);
//
//                }
//                cal.add(Calendar.DATE, +2);
//                day = new java.sql.Date(cal.getTimeInMillis());
//                dayCount++;
//            }
//
//        }
//    }
//
//    @PostConstruct
//    public void func() {
//        SimulateHistory(100);
//    }

}

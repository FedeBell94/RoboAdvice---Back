package it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate;

import it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.quandl.Quandl;
import it.uiip.digitalgarage.roboadvice.persistence.model.*;
import it.uiip.digitalgarage.roboadvice.persistence.repository.*;
import it.uiip.digitalgarage.roboadvice.utils.Logger;
import it.uiip.digitalgarage.roboadvice.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: abstract the quandl data retrieving, multithread

@Service
@SuppressWarnings("unused")
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

    /**
     * The default start worth of a user. It is set to 10000.
     */
    private static final BigDecimal DEFAULT_START_WORTH = new BigDecimal(10000);

    @Override
    public void executeUpdateTask() {

        // Finds all assets
        final Iterable<Asset> assets = assetRepository.findAll();

        // Finds all users
        final Iterable<User> users = userRepository.findAll();

        // Dates
        final Date today = Utils.getToday();
        final Date yesterday = Utils.getYesterday();

        // #1: update quandl data
        updateQuanldData(assets);

        // For each asset find the latest price
        final Map<Integer, BigDecimal> latestPrices = findAssetsLatestPrice(assets);

        for (User currUser : users) {
            // Find the portfolio of yesterday for the current user
            List<Portfolio> userPortfolio = portfolioRepository.findByUserAndDate(currUser, yesterday);

            // Case of brand new user
            if (userPortfolio.isEmpty()) {
                // #2: create portfolio for fresh(new) users
                createPortfolio(currUser, assets, DEFAULT_START_WORTH, latestPrices);
            } else {

                // Finds the active strategy of the current user
                List<Strategy> userStrategy = strategyRepository.findByUserAndActiveTrue(currUser);

                // Check if the user strategy was changed yesterday
                if (userStrategy.get(0).getStartingDate().toString().equals(yesterday.toString())) {

                    // #4: compute portfolio for 'old' users which has changed the strategy (same as #2)
                    BigDecimal userWorth = computeWorth(userPortfolio);
                    createPortfolio(currUser, assets, userWorth, latestPrices);
                } else {
                    // #3: update portfolio for 'old' users which didn't change the strategy yesterday

                    // Check if the user uses and auto-balancing strategy
                    if (currUser.isAutoBalancing()) {
                        BigDecimal userWorth = computeWorth(userPortfolio);
                        createPortfolio(currUser, assets, userWorth, latestPrices);
                    } else {
                        updatePortfolio(userPortfolio, latestPrices);
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
    private BigDecimal computeWorth(final Iterable<Portfolio> portfolios) {

        //TODO questo è il worth di ieri!! ricalcolalo tutto come somma dei value del portfolio per il valore dell'asset
        // di oggi!!!!

        BigDecimal worth = new BigDecimal(0);
        for (Portfolio currPortfolio : portfolios) {
            worth = worth.add(currPortfolio.getValue());
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
    private void updatePortfolio(final Iterable<Portfolio> portfolios, Map<Integer, BigDecimal> latestPrices) {


        final Date currDate = Utils.getToday();
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
    private Map<Integer, BigDecimal> findAssetsLatestPrice(final Iterable<Asset> assets) {
        Map<Integer, BigDecimal> latestPrices = new HashMap<>();
        for (Asset curr : assets) {
            Data data = dataRepository.findFirst1ByAssetOrderByDateDesc(curr);
            latestPrices.put(data.getAsset().getId(), data.getValue());
        }
        return latestPrices;
    }

    /**
     * Update the asset data in the database with Quanld data
     *
     * @param assets
     *         The assets to update.
     */
    private void updateQuanldData(Iterable<Asset> assets) {
        Quandl quandl = new Quandl();
        for (Asset asset : assets) {
            quandl.callDailyQuandl(asset, dataRepository);
        }
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
    private void createPortfolio(final User user, final Iterable<Asset> assets, final BigDecimal totalMoney,
                                 final Map<Integer, BigDecimal> latestPrices) {

        final Date currDate = Utils.getToday();
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
                    BigDecimal assetUnits = assetMoney.divide(latestAssetPrice, 8, RoundingMode.HALF_DOWN);

                    Logger.debug(DailyTaskUpdate.class, "" + assetMoney + " " + latestAssetPrice + " " + assetUnits);

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

}

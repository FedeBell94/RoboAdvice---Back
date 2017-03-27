package it.uiip.digitalgarage.roboadvice.service;

import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Portfolio;
import it.uiip.digitalgarage.roboadvice.persistence.model.Strategy;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import it.uiip.digitalgarage.roboadvice.utils.RoboAdviceConstant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class performs the core task of the application Robo-Advice. All methods are static, and the class is declared
 * final ini order to avoid others to extend this class and change the core behaviour. In addition to this, the default
 * constructor of this class is private.
 */
public final class CoreTask {

    private static final Log LOGGER = LogFactory.getLog(CoreTask.class);

    /**
     * This static method execute the core behaviour of the application. If the user passed is new, a new portfolio will
     * be created; if the user is not new and he has changed the strategy in the previous day, all the money will be
     * reallocated in order to fit the new strategy. Finally the default case: the portfolio of the user is updated,
     * changing the portfolio with the current asset prices, and eventually re-balancing his portfolio in order to
     * follow the chosen strategy.
     *
     * @param user            The {@link User} owner of the portfolio.
     * @param lastPortfolio   The last {@link Portfolio} computed for the user.
     * @param activeStrategy  The active {@link Strategy} of the user.
     * @param assetPrice      The latest asset prices.
     * @param assets          All the {@link Asset}.
     * @param worthToAllocate The worth to allocate in the portfolio. If null is passed, the default worth will be
     *                        applied in case of creation of new strategy.
     *
     * @return The next portfolio computed for the user.
     */
    public static List<Portfolio> executeTask(final User user, final List<Portfolio> lastPortfolio,
                                              final List<Strategy> activeStrategy,
                                              final Map<Long, BigDecimal> assetPrice,
                                              final Iterable<Asset> assets,
                                              final BigDecimal worthToAllocate) {
        if (isNewUser(lastPortfolio)) {
            BigDecimal worth = worthToAllocate == null ? RoboAdviceConstant.DEFAULT_START_WORTH : worthToAllocate;
            return createPortfolio(user, assets, worth, assetPrice, activeStrategy);
        } else if (hasChangedStrategy(user, activeStrategy)) {
            BigDecimal dayWorth = computeWorth(lastPortfolio, assetPrice);
            return createPortfolio(user, assets, dayWorth, assetPrice, activeStrategy);
        } else {
            List<Portfolio> computedPortfolio = updatePortfolio(lastPortfolio, assetPrice, activeStrategy);
            if (needToReBalance(computedPortfolio, activeStrategy)) {
                reBalancePortfolio(computedPortfolio, assetPrice);
            }
            return computedPortfolio;
        }
    }

    /**
     * This method update the portfolio of the user. This method take the last portfolio of the user and returns a new
     * portfolio with all the data computed to follow the new asset prices.
     */
    private static List<Portfolio> updatePortfolio(final List<Portfolio> lastPortfolio,
                                                   final Map<Long, BigDecimal> assetPrice,
                                                   final List<Strategy> activeStrategy) {
        Date date = new CustomDate(lastPortfolio.get(0).getDate()).moveOneDayForward().getDateSql();
        List<Portfolio> returnList = new ArrayList<>(assetPrice.size());
        Map<Long, BigDecimal> strategyPercentage = new HashMap<>();
        for (Strategy currStrategy : activeStrategy) {
            strategyPercentage.put(currStrategy.getAssetClass().getId(), currStrategy.getPercentage());
        }

        for (Portfolio currPortfolio : lastPortfolio) {
            BigDecimal assetMoney = currPortfolio.getUnit()
                    .multiply(assetPrice.get(currPortfolio.getAsset().getId()))
                    .setScale(4, BigDecimal.ROUND_HALF_UP);
            BigDecimal globalInfluence = currPortfolio.getAsset().getFixedPercentage()
                    .multiply(strategyPercentage.get(currPortfolio.getAssetClass().getId()))
                    .divide(new BigDecimal(10000), 4, BigDecimal.ROUND_HALF_UP);
            returnList.add(Portfolio.builder()
                    .user(currPortfolio.getUser())
                    .assetClass(currPortfolio.getAssetClass())
                    .asset(currPortfolio.getAsset())
                    .unit(currPortfolio.getUnit())
                    .value(assetMoney)
                    .date(date)
                    .globalInfluence(globalInfluence)
                    .build());
        }
        return returnList;
    }

    /**
     * This method return a new portfolio created for the user, with the worth passed as argument.
     */
    private static List<Portfolio> createPortfolio(final User user, final Iterable<Asset> assets,
                                                   final BigDecimal worth,
                                                   final Map<Long, BigDecimal> assetPrice,
                                                   final List<Strategy> activeStrategy) {

        List<Portfolio> insertList = new ArrayList<>();
        for (Strategy currStrategy : activeStrategy) {
            for (Asset currAsset : assets) {
                if (currAsset.getAssetClass().getId().equals(currStrategy.getAssetClass().getId())) {

                    BigDecimal assetMoney = worth.multiply(currStrategy.getPercentage())
                            .multiply(currAsset.getFixedPercentage())
                            .setScale(4, BigDecimal.ROUND_HALF_UP)
                            .divide(new BigDecimal(10000), 4, BigDecimal.ROUND_HALF_UP);

                    BigDecimal latestAssetPrice = assetPrice.get(currAsset.getId());
                    BigDecimal assetUnits = assetMoney.divide(latestAssetPrice, 4, RoundingMode.HALF_UP);

                    insertList.add(Portfolio.builder()
                            .user(user)
                            .assetClass(currAsset.getAssetClass())
                            .asset(currAsset)
                            .unit(assetUnits)
                            .value(assetMoney)
                            .date(new CustomDate(user.getLastPortfolioComputation()).moveOneDayForward().getDateSql())
                            .build());
                }
            }
        }
        return insertList;
    }

    /**
     * This method compute the worth of the portfolio with the prices of the asset passed.
     */
    private static BigDecimal computeWorth(final List<Portfolio> portfolios, final Map<Long, BigDecimal> assetPrice) {
        BigDecimal worth = new BigDecimal(0);
        for (Portfolio currPortfolio : portfolios) {
            BigDecimal singleWorth =
                    currPortfolio.getUnit().multiply(assetPrice.get(currPortfolio.getAsset().getId()));
            worth = worth.add(singleWorth);
        }
        return worth;
    }

    /**
     * This method checks if the passed portfolio needs to be rebalanced to follow the passed strategy. The percentage
     * boundaries to determine if is needed to re-balance or not is defined in the {@link RoboAdviceConstant} class.
     */
    private static boolean needToReBalance(List<Portfolio> portfolio, List<Strategy> strategy) {
        BigDecimal totalWorth = BigDecimal.ZERO;
        Map<Long, BigDecimal> assetClassWorth = new HashMap<>();
        for (Portfolio currPortfolio : portfolio) {
            totalWorth = totalWorth.add(currPortfolio.getValue());
            Long currAssetClassId = currPortfolio.getAssetClass().getId();
            BigDecimal currWorth = assetClassWorth.get(currAssetClassId);
            if (currWorth == null) {
                assetClassWorth.put(currAssetClassId, currPortfolio.getValue());
            } else {
                currWorth = currWorth.add(currPortfolio.getValue());
                assetClassWorth.put(currAssetClassId, currWorth);
            }
        }

        for (Strategy currStrategy : strategy) {
            BigDecimal actualPercentage = assetClassWorth.get(currStrategy.getAssetClass().getId())
                    .divide(totalWorth, 4, BigDecimal.ROUND_HALF_UP);
            BigDecimal strategyPercentage = currStrategy.getPercentage()
                    .divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP);
            BigDecimal percentageDifference = actualPercentage
                    .subtract(strategyPercentage)
                    .abs();
            if (percentageDifference.compareTo(RoboAdviceConstant.RE_BALANCING_BOUNDARIES) > 0) {
                LOGGER.debug("Current portfolio needs to be rebalanced on " + portfolio.get(0).getDate());
                return true;
            }
        }
        return false;
    }

    /**
     * Compute the re-balance algorithm. This method modifies directly the portfolio passed as input!!!
     */
    private static void reBalancePortfolio(List<Portfolio> portfolio, Map<Long, BigDecimal> assetPrice) {
        BigDecimal totalWorth = BigDecimal.ZERO;
        for (Portfolio currPortfolio : portfolio) {
            totalWorth = totalWorth.add(currPortfolio.getValue());
        }

        for (Portfolio currPortfolio : portfolio) {
            BigDecimal newWorth = totalWorth
                    .multiply(currPortfolio.getGlobalInfluence());
            BigDecimal currAssetPrice = assetPrice.get(currPortfolio.getAsset().getId());
            BigDecimal newUnits = newWorth
                    .divide(currAssetPrice, 4, BigDecimal.ROUND_HALF_UP);
            currPortfolio.setValue(newWorth);
            currPortfolio.setUnit(newUnits);
        }
    }

    /**
     * Check if the user is new (the user doesn't have a portfolio yet).
     */
    private static boolean isNewUser(List<Portfolio> portfolio) {
        return portfolio == null || portfolio.isEmpty();
    }

    /**
     * Check if the user has changed the strategy yesterday.
     */
    private static boolean hasChangedStrategy(User user, List<Strategy> activeStrategy) {
        return user.getLastStrategyComputed() != null &&
                user.getLastStrategyComputed().compareTo(activeStrategy.get(0).getStartingDate()) != 0;
    }

    /**
     * The constructor of this class is private in order to make not possible to instantiate this utility class.
     */
    private CoreTask() {
    }
}

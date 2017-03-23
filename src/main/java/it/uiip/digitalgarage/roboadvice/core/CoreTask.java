package it.uiip.digitalgarage.roboadvice.core;

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

public class CoreTask {

    private static final Log LOGGER = LogFactory.getLog(CoreTask.class);

    public static List<Portfolio> executeTask(final User user, final List<Portfolio> lastPortfolio,
                                              final List<Strategy> activeStrategy,
                                              final Map<Long, BigDecimal> assetPrice,
                                              final Iterable<Asset> assets,
                                              final BigDecimal worthToAllocate) {
        // TODO more readable code here
        if (lastPortfolio.isEmpty()) {
            BigDecimal worth = worthToAllocate == null ? RoboAdviceConstant.DEFAULT_START_WORTH : worthToAllocate;
            return createPortfolio(user, assets, worth, assetPrice, activeStrategy);
        } else if (user.getLastStrategyComputed() != null &&
                user.getLastStrategyComputed().compareTo(activeStrategy.get(0).getStartingDate()) != 0) {
            BigDecimal dayWorth = computeWorth(lastPortfolio, assetPrice);
            return createPortfolio(user, assets, dayWorth, assetPrice, activeStrategy);
        } else {
            List<Portfolio> computedPortfolio = updatePortfolio(lastPortfolio, assetPrice);
            if (needToReBalance(computedPortfolio, activeStrategy)) {
                // TODO rebalance algorithm
            }
            return computedPortfolio;
        }
    }

    private static List<Portfolio> updatePortfolio(final List<Portfolio> lastPortfolio,
                                                   final Map<Long, BigDecimal> assetPrice) {
        Date date = new CustomDate(lastPortfolio.get(0).getDate()).moveOneDayForward().getDateSql();
        List<Portfolio> returnList = new ArrayList<>(assetPrice.size());
        for (Portfolio currPortfolio : lastPortfolio) {
            BigDecimal assetMoney = currPortfolio.getUnit().multiply(assetPrice.get(currPortfolio.getAsset().getId()));
            returnList.add(Portfolio.builder()
                    .user(currPortfolio.getUser())
                    .assetClass(currPortfolio.getAssetClass())
                    .asset(currPortfolio.getAsset())
                    .unit(currPortfolio.getUnit())
                    .value(assetMoney)
                    .date(date)
                    .build());
        }
        return returnList;
    }

    private static List<Portfolio> createPortfolio(final User user, final Iterable<Asset> assets,
                                                   final BigDecimal worth,
                                                   final Map<Long, BigDecimal> assetPrice,
                                                   final List<Strategy> activeStrategy) {

        List<Portfolio> insertList = new ArrayList<>();
        for (Strategy currStrategy : activeStrategy) {
            for (Asset currAsset : assets) {
                if (currAsset.getAssetClass().getId().equals(currStrategy.getAssetClass().getId())) {

                    BigDecimal assetMoney = worth.multiply(currStrategy.getPercentage())
                            .multiply(currAsset.getFixedPercentage()).divide(new BigDecimal(10000), 4);

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

    private static BigDecimal computeWorth(final List<Portfolio> portfolios, final Map<Long, BigDecimal> assetPrice) {
        BigDecimal worth = new BigDecimal(0);
        for (Portfolio currPortfolio : portfolios) {
            BigDecimal singleWorth =
                    currPortfolio.getUnit().multiply(assetPrice.get(currPortfolio.getAsset().getId()));
            worth = worth.add(singleWorth);
        }
        return worth;
    }

    private static boolean needToReBalance(List<Portfolio> portfolio, List<Strategy> strategy) {
        Map<Long, BigDecimal> assetClassWorth = new HashMap<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Portfolio currPortfolio : portfolio) {
            Long currAssetClassId = currPortfolio.getAssetClass().getId();
            BigDecimal worth = assetClassWorth.get(currAssetClassId);
            if (worth == null) {
                assetClassWorth.put(currAssetClassId, currPortfolio.getValue());
            } else {
                assetClassWorth.put(currAssetClassId, worth.add(currPortfolio.getValue()));
            }
            total = total.add(currPortfolio.getValue());
        }

        for (Map.Entry currAssetClass : assetClassWorth.entrySet()) {
            BigDecimal assetClassPercentage = ((BigDecimal) currAssetClass.getValue())
                    .divide(total, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            Strategy currStrategy = null;
            for (Strategy s : strategy) {
                if (s.getAssetClass().getId() == currAssetClass.getKey()) {
                    currStrategy = s;
                    break;
                }
            }
            BigDecimal strategyPercentage = currStrategy.getPercentage();

            if (assetClassPercentage.subtract(strategyPercentage).abs()
                    .compareTo(RoboAdviceConstant.RE_BALANCING_BOUNDARIES) > 0) {
                LOGGER.debug("Re-balancing portfolio on date " + portfolio.get(0).getDate() +
                        " needed (asset class percentage: " + assetClassPercentage +
                        ", expected strategy percentage: " + strategyPercentage + " on asset class id " +
                        currAssetClass.getKey());
                return true;
            }
        }
        return false;
    }


    private CoreTask() {
    }
}

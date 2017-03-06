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


@Service
@SuppressWarnings("unused")
public class DailyTaskUpdate implements IDailyTaskUpdate {


    @Autowired
    public StrategyRepository strategyRepository;

    @Autowired
    public PortfolioRepository portfolioRepository;

    @Autowired
    public AssetRepository assetRepository;

    @Autowired
    public DataRepository dataRepository;

    @Autowired
    public UserRepository userRepository;


    @Override
    public void executeUpdateTask() {

        // Find all assets
        final Iterable<Asset> assets = assetRepository.findAll();

        // Find all users
        final Iterable<User> users = userRepository.findAll();

        // For each asset find the latest price
        final Map<Integer, BigDecimal> latestPrices = findAssetsLatestPrice(assets);

        // Dates
        final Date today = Utils.getToday();
        final Date yesterday = Utils.getYesterday();

        // #1: update quandl data
        updateQuanldData(assets);

        for (User currUser : users) {
            // Find the portfolio of yesterday for the current user
            List<Portfolio> userPortfolio = portfolioRepository.findByUserAndDate(currUser, yesterday);

            // Case of brand new user
            if (userPortfolio.size() == 0) {
                // #2: create portfolio for fresh(new) users
                createPortfolio(currUser, assets, new BigDecimal(10000), latestPrices, today);
            } else {

                // #3: update portfolio for 'old' users

                // #4: compute portfolio for 'old[ users which has changes the strategy (same as #2)
            }
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

    private void updateQuanldData(Iterable<Asset> assets){
        Quandl quandl = new Quandl();
        for (Asset asset : assets) {
            quandl.callDailyQuandl(asset, dataRepository);
        }
    }

    private void createPortfolio(final User user, final Iterable<Asset> assets, final BigDecimal totalMoney,
                                 final Map<Integer, BigDecimal> latestPrices, final Date currDate) {
        // Retrieve the active strategy of the user
        List<Strategy> userStrategy = strategyRepository.findByUserAndActiveTrue(user);

        for (Strategy currStrategy : userStrategy) {
            for (Asset currAsset : assets) {
                if (currAsset.getAssetClass().equals(currStrategy.getAssetClass())) {

                    /*
                    moneyForAsset = totalMoney*(assetClassStrategyPerc)*(assetDistributionPerc)

                    moneyForAsset = totalMoney*(assetClassStrategy / 100)*(assetDistribution / 100)

                    moneyForAsset = totalMoney*(assetClassStrategy * assetDistribution)/10000
                     */
                    BigDecimal assetMoney = totalMoney.multiply(currStrategy.getPercentage())
                            .multiply(currAsset.getFixedPercentage()).divide(new BigDecimal(10000), 4);

                    BigDecimal latestAssetPrice = latestPrices.get(currAsset.getId());
                    BigDecimal assetUnits = assetMoney.divide(latestAssetPrice,2, RoundingMode.HALF_UP);

                    Logger.error(DailyTaskUpdate.class, "" + assetMoney + " " + latestAssetPrice + " " + assetUnits);

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

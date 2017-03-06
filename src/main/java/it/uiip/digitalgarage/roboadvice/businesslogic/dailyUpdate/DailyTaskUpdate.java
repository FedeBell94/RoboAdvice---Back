package it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate;

import it.uiip.digitalgarage.roboadvice.persistence.model.*;
import it.uiip.digitalgarage.roboadvice.persistence.repository.*;
import it.uiip.digitalgarage.roboadvice.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DailyTaskUpdate {


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

//    private static DailyTaskUpdate instance = null;
//
//    private DailyTaskUpdate () {}
//
//    public static synchronized DailyTaskUpdate getInstance(){
//        if(instance == null){
//            instance = new DailyTaskUpdate();
//        }
//        return instance;
//    }

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


        // #1: update quandl data TODO

        for (User currUser : users) {
            // Find the latest portfolio
            final List<Portfolio> userPortfolio = portfolioRepository.findByUserAndDate(currUser, yesterday);

            // Case of brand new user
            if(userPortfolio.size() == 0){
                // #2 A: create portfolio for fresh(new) users
                createPortfolio(currUser, assets, new BigDecimal(10000), latestPrices, today);
            } else {

                // #2 B: update portfolio for 'old' users

                // #2 C: compute portfolio for 'old[ users which has changes the strategy (same as #2 A)
            }
        }
    }

    private Map<Integer, BigDecimal> findAssetsLatestPrice(final Iterable<Asset> assets) {
        Map<Integer, BigDecimal> latestPrices = new HashMap<>();
        for (Asset curr : assets) {
            Data data = dataRepository.findFirst1ByAssetOrderByDateDesc(curr);
            latestPrices.put(data.getId(), data.getValue());
        }
        return latestPrices;
    }

    private void createPortfolio(final User user, final Iterable<Asset> assets, final BigDecimal totalMoney,
                                 final Map<Integer, BigDecimal> latestPrices, Date currDate) {
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
                    BigDecimal assetUnits = assetMoney.divide(latestAssetPrice, 4);

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

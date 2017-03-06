package it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate;

import it.uiip.digitalgarage.roboadvice.businesslogic.economy.Buy;
import it.uiip.digitalgarage.roboadvice.businesslogic.quandl.Quandl;
import it.uiip.digitalgarage.roboadvice.persistence.model.*;
import it.uiip.digitalgarage.roboadvice.persistence.repository.*;
import it.uiip.digitalgarage.roboadvice.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.springframework.boot.autoconfigure.SpringBootApplication;


import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@SpringBootApplication
public class DailyTaskUpdate {


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
        Iterable<Asset> assets = assetRepository.findAll();

        // Find all users
        Iterable<User> users = userRepository.findAll();

        // For each asset find the latest price
        Map<Integer, BigDecimal> latestPrices = findAssetsLatestPrice(assets);

        // Dates
        Date today = Utils.getToday();
        Date yesterday = Utils.getYesterday();


        // #1: update quandl data TODO
        Quandl quandl = new Quandl();
        for (Asset asset : assets) {
            quandl.callDailyQuandl(asset,dataRepository);
        }


        for (User currUser : users) {
            // Find the latest portfolio
            List<Portfolio> userPortfolio = portfolioRepository.findByUserAndDate(currUser, yesterday);

            // Case of brand new user
            if (userPortfolio.size() == 0) {
                // #2 A: create portfolio for fresh(new) users
                //createPortfolio(currUser, assets, new BigDecimal(10000), latestPrices, today);
                Buy b = new Buy();
                b.buyAssets(new BigDecimal(10000),currUser,strategyRepository, portfolioRepository, assetRepository, dataRepository);

            } else {

                // #2 B (3): update portfolio for 'old' users

                // #2 C (4): compute portfolio for 'old[ users which has changes the strategy (same as #2 A)
            }
        }
    }

    private Map<Integer, BigDecimal> findAssetsLatestPrice(Iterable<Asset> assets) {
        Map<Integer, BigDecimal> latestPrices = new HashMap<>();
        for (Asset curr : assets) {
            Data data = dataRepository.findFirst1ByAssetOrderByDateDesc(curr);
            latestPrices.put(data.getId(), data.getValue());
        }
        return latestPrices;
    }

//    private void createPortfolio(User user, Iterable<Asset> assets, BigDecimal totalMoney,
//                                 Map<Integer, BigDecimal> latestPrices, Date currDate) {
//        // Retrieve the active strategy of the user
//        List<Strategy> userStrategy = strategyRepository.findByUserAndActiveTrue(user);
//
//        for (Strategy currStrategy : userStrategy) {
//            for (Asset currAsset : assets) {
//                if (currAsset.getAssetClass().equals(currStrategy.getAssetClass())) {
//
//                    /*
//                    moneyForAsset = totalMoney*(assetClassStrategyPerc)*(assetDistributionPerc)
//
//                    moneyForAsset = totalMoney*(assetClassStrategy / 100)*(assetDistribution / 100)
//
//                    moneyForAsset = totalMoney*(assetClassStrategy * assetDistribution)/10000
//                     */
//                    BigDecimal assetMoney = totalMoney.multiply(currStrategy.getPercentage())
//                            .multiply(currAsset.getFixedPercentage()).divide(new BigDecimal(10000), 4);
//
//                    BigDecimal latestAssetPrice = latestPrices.get(currAsset.getId());
//                    BigDecimal assetUnits = assetMoney.divide(latestAssetPrice, 4);
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

    @PostConstruct
    void func() {
        //DailyTaskUpdate dailyTaskUpdate = new DailyTaskUpdate();
        executeUpdateTask();
    }
}

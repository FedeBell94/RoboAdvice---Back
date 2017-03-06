package it.uiip.digitalgarage.roboadvice.businesslogic.economy;

import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Portfolio;
import it.uiip.digitalgarage.roboadvice.persistence.model.Strategy;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.PortfolioRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.StrategyRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Simone on 03/03/2017.
 */
public class Buy {



//    @Autowired
//    private StrategyRepository strategyRepository;
//
//    @Autowired
//    private PortfolioRepository portfolioRepository;
//
//    @Autowired
//    private AssetRepository assetRepository;

    public void buyAssets(BigDecimal totalMoney, User user, StrategyRepository strategyRepository, PortfolioRepository portfolioRepository, AssetRepository assetRepository, DataRepository dataRepository) {

        List<Strategy> str = strategyRepository.findByUserAndActiveTrue(user);
        java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());

        ArrayList<Asset> acl;
        Evaluation eval = new Evaluation();

        //ASSET CLASS
        for (int x = 0; x < str.size(); x++) {

            acl = assetRepository.findByAssetClass(str.get(x).getAssetClass());

            BigDecimal moneyForClass = totalMoney.multiply(str.get(x).getPercentage().divide(new BigDecimal(100)));

            //ASSET
            for (int y = 0; y < acl.size(); y++) {

                BigDecimal moneyForAsset = moneyForClass.multiply(acl.get(y).getFixedPercentage().divide(new BigDecimal(100)));
                BigDecimal assetValue = eval.getLastValue(acl.get(y),portfolioRepository,dataRepository);
                BigDecimal units = moneyForAsset.divide(assetValue,2, RoundingMode.HALF_UP);

                portfolioRepository.save(Portfolio.builder()
                        .user(user)
                        .assetClass(str.get(x).getAssetClass())
                        .asset(acl.get(y))
                        .unit(units)
                        .value(units.multiply(assetValue))
                        .date(date)
                        .build());
            }

        }

    }

    public static void main(String[] args) {



    }
}

package it.uiip.digitalgarage.roboadvice.businesslogic.economy;

import it.uiip.digitalgarage.roboadvice.persistence.model.*;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.PortfolioRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.StrategyRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Simone on 03/03/2017.
 */
public class Buy {

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private AssetRepository assetRepository;

    public void buyAssets(BigDecimal totalMoney, User user) {

        ArrayList<Strategy> str = strategyRepository.findByUserAndActive(user, true);
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
                BigDecimal assetValue = eval.getLastValue(acl.get(y));
                BigDecimal units = moneyForAsset.divide(assetValue);

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
}

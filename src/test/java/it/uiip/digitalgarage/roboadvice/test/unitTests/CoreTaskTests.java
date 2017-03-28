package it.uiip.digitalgarage.roboadvice.test.unitTests;

import it.uiip.digitalgarage.roboadvice.persistence.model.*;
import it.uiip.digitalgarage.roboadvice.service.CoreTask;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Simone on 28/03/2017.
 */
public class CoreTaskTests {

    private static final Log LOGGER = LogFactory.getLog(CoreTask.class);



    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    public User testUser;
    public List<Portfolio> testPortfolio;
    public Portfolio portfolio;
    public List<Strategy> strategyList;
    public Strategy strategy;
    public Map<Long,BigDecimal> assetPrice;
    public List<Asset> assetList;
    public CustomDate customDate;

    @Before
    public void before() {
        customDate = new CustomDate(LocalDate.now());
        testUser = User.builder().id(1L).username("testUser").password("12345").nickname("testUser")
                .lastStrategyComputed(customDate.getYesterdaySql())
                .lastPortfolioComputation(Date.valueOf(LocalDate.now())).build();
        assetList = new ArrayList<>();
        AssetClass assetClass = AssetClass.builder().id(1L).name("JUNITassetClass").build();
        Asset asset =
                Asset.builder().assetClass(assetClass).name("unitTest").id(1L).fixedPercentage(new BigDecimal("100"))
                        .quandlColumn("before").quandlId(1).quandlKey("WIKI/unitTest").build();
        assetList.add(asset);
        testPortfolio = new ArrayList<>();
        portfolio =
                Portfolio.builder().asset(asset).value(new BigDecimal("900")).unit(new BigDecimal("3")).id(1L)
                        .user(testUser).date(Date.valueOf(LocalDate.now())).assetClass(assetClass).build();

        strategyList = new ArrayList<>();
        strategy = Strategy.builder().assetClass(assetClass).user(testUser).active(true).id(1L)
                .percentage(new BigDecimal("100")).startingDate(Date.valueOf(LocalDate.now())).build();
        strategyList.add(strategy);
        assetPrice = new HashMap<>();
        assetPrice.put(1L,new BigDecimal("900"));
    }

    @Test
    public void testExecuteTaskNewUser() {

        List<Portfolio> result = CoreTask.executeTask(testUser,testPortfolio,strategyList,assetPrice,assetList,new BigDecimal("10000"));

        assertEquals(result.get(0).getValue().compareTo(new BigDecimal("10000")) , 0 );

    }

    @Test
    public void testExecuteTaskNewStrategy() {

        testUser.setIsNewUser(false);
        testPortfolio.add(portfolio);
        List<Portfolio> result = CoreTask.executeTask(testUser,testPortfolio,strategyList,assetPrice,assetList,new BigDecimal("10000"));

        assertEquals(result.get(0).getAsset().getId() == 1L , true  );
    }

    @Test
    public void testExecuteTaskDefault() {

        testUser.setLastStrategyComputed(null);
        strategy.setStartingDate(CustomDate.getToday().getYesterdaySql());
        strategyList.clear();
        strategyList.add(strategy);
        testPortfolio.add(portfolio);
        List<Portfolio> result = CoreTask.executeTask(testUser,testPortfolio,strategyList,assetPrice,assetList,new BigDecimal("10000"));

        assertEquals(result.get(0).getAsset().getId() == 1L , true  );
    }

    @Test
    public void testExecuteTaskRebalance() {

        testUser.setLastStrategyComputed(null);
        strategy.setStartingDate(CustomDate.getToday().getYesterdaySql());;

        AssetClass ac1 = AssetClass.builder().id(1L).build();
        AssetClass ac2 = AssetClass.builder().id(2L).build();

        Strategy s1 = Strategy.builder().id(1L).assetClass(ac1).user(testUser).percentage(BigDecimal.valueOf(70))
                .startingDate(CustomDate.getToday().getYesterdaySql()).build();
        Strategy s2 = Strategy.builder().id(2L).assetClass(ac2).user(testUser).percentage(BigDecimal.valueOf(30))
                .startingDate(CustomDate.getToday().getYesterdaySql()).build();
        strategyList.clear();
        strategyList.add(s1);
        strategyList.add(s2);

        Asset a1 = Asset.builder().id(1L).assetClass(ac1).fixedPercentage(BigDecimal.valueOf(100)).build();
        Asset a2 = Asset.builder().id(2L).assetClass(ac2).fixedPercentage(BigDecimal.valueOf(40)).build();
        Asset a3 = Asset.builder().id(3L).assetClass(ac2).fixedPercentage(BigDecimal.valueOf(60)).build();

        Portfolio p1 = Portfolio.builder().asset(a1).assetClass(ac1).unit(BigDecimal.valueOf(800)).value(BigDecimal.valueOf(800))
                .date(CustomDate.getToday().getYesterdaySql()).build();
        Portfolio p2 = Portfolio.builder().asset(a2).assetClass(ac2).unit(BigDecimal.valueOf(200)).value(BigDecimal.valueOf(200))
                .date(CustomDate.getToday().getYesterdaySql()).build();
        Portfolio p3 = Portfolio.builder().asset(a3).assetClass(ac2).unit(BigDecimal.valueOf(0)).value(BigDecimal.valueOf(0))
                .date(CustomDate.getToday().getYesterdaySql()).build();
        testPortfolio.clear();
        testPortfolio.add(p1);
        testPortfolio.add(p2);
        testPortfolio.add(p3);

        assetPrice.clear();
        assetPrice.put(1L, BigDecimal.ONE);
        assetPrice.put(2L, BigDecimal.ONE);
        assetPrice.put(3L, BigDecimal.ONE);

        List<Portfolio> result = CoreTask.executeTask(testUser,testPortfolio,strategyList,assetPrice,assetList,null);

        for(Portfolio p : result){
            BigDecimal val = p.getValue();
            switch((int) (long) p.getAsset().getId()){
                case 1:
                    assertTrue(val.compareTo(BigDecimal.valueOf(700)) == 0);
                    break;
                case 2:
                    assertTrue(val.compareTo(BigDecimal.valueOf(120)) == 0);
                    break;
                case 3:
                    assertTrue(val.compareTo(BigDecimal.valueOf(180)) == 0);
                    break;
                default:
                    assertTrue(false);
            }
        }
    }
}

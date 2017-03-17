package it.uiip.digitalgarage.roboadvice.unitTests;

import it.uiip.digitalgarage.roboadvice.Application;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.NightlyTask;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater.IDataUpdater;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dateProvider.DateProvider;
import it.uiip.digitalgarage.roboadvice.persistence.model.*;
import it.uiip.digitalgarage.roboadvice.persistence.repository.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by Simone on 16/03/2017.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {PersistenceContext.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
public class NightlyTaskTests {


    private static final Log LOGGER = LogFactory.getLog(Application.class);

    @Mock
    private StrategyRepository strategyRepositoryMock;

    @Mock
    private PortfolioRepository portfolioRepositoryMock;

    @Mock
    private AssetRepository assetRepositoryMock;

    @Mock
    private DataRepository dataRepositoryMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private IDataUpdater dataUpdaterMock;

    @Mock
    private DateProvider dateProviderMock;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private List<User> users;

    @Before
    public void before() {

        User testUser = User.builder().id(1L).username("testUser").password("12345").nickname("testUser").lastPortfolioComputation(Date.valueOf(LocalDate.now())).build();

        List<Asset> assetList = new ArrayList<>();
        AssetClass assetClass = AssetClass.builder().id(0L).name("JUNITassetClass").build();
        Asset asset = Asset.builder().assetClass(assetClass).name("unitTest").id(0L).fixedPercentage(new BigDecimal("100")).quandlColumn("before").quandlId(1).quandlKey("WIKI/unitTest").build();
        assetList.add(asset);

        List<Data> dataList = new ArrayList<>();
        Data data = Data.builder().asset(asset).date(Date.valueOf(LocalDate.now())).value(new BigDecimal("300")).id(0L).build();
        dataList.add(data);

        users = new ArrayList<>();
        users.add(testUser);

        List<Portfolio> portfolioList = new ArrayList<>();
        Portfolio portfolio = Portfolio.builder().asset(asset).value(new BigDecimal("900")).unit(new BigDecimal("3")).id(0L).user(testUser).date(Date.valueOf(LocalDate.now())).assetClass(assetClass).build();

        List<Strategy> strategyList = new ArrayList<>();
        Strategy strategy = Strategy.builder().assetClass(assetClass).user(testUser).active(true).id(1L).percentage(new BigDecimal("100")).startingDate(Date.valueOf(LocalDate.now())).build();
        strategyList.add(strategy);

        when(assetRepositoryMock.findAll()).thenReturn(assetList);
        when(dateProviderMock.getYesterday()).thenReturn(getYesterday());
        when(dataRepositoryMock.findByDate(any())).thenReturn(dataList);
        when(portfolioRepositoryMock.findByUserAndDate(any(), any())).thenReturn(portfolioList);
        when(strategyRepositoryMock.findByUserAndActiveTrue(any())).thenReturn(strategyList);
        when(dataRepositoryMock.findTop1ByDateBeforeAndAssetOrderByDateDesc(any(), any())).thenReturn(data);

    }

    @Test
    public void testCreationNightlyTask() {

        NightlyTask nightlyTask = new NightlyTask(strategyRepositoryMock, portfolioRepositoryMock, assetRepositoryMock, dataRepositoryMock, userRepositoryMock, dataUpdaterMock);

        assertEquals(
                "class it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.NightlyTask",
                nightlyTask.getClass().toString());

    }

    @Test
    public void testNightlyTask() {

        NightlyTask nightlyTask = new NightlyTask(strategyRepositoryMock, portfolioRepositoryMock, assetRepositoryMock, dataRepositoryMock, userRepositoryMock, dataUpdaterMock);

        nightlyTask.executeNightlyTask(dateProviderMock, users);

        assertTrue(true);

    }

    public Date getYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return new Date(calendar.getTime().getTime());
    }

}

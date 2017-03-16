package it.uiip.digitalgarage.roboadvice.tests;

import it.uiip.digitalgarage.roboadvice.Application;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.NightlyTask;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater.IDataUpdater;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater.Quandl.QuandlDataSource;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dateProvider.DateProvider;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.persistence.repository.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by Simone on 16/03/2017.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {PersistenceContext.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
public class NightlyTaskTest {


    private static final Log LOGGER = LogFactory.getLog(Application.class);

    @Mock
    StrategyRepository strategyRepositoryMock;

    @Mock
    PortfolioRepository portfolioRepositoryMock;

    @Mock
    AssetRepository assetRepositoryMock;

    @Mock
    DataRepository dataRepositoryMock;

    @Mock
    UserRepository userRepositoryMock;

    @Mock
    IDataUpdater dataUpdaterMock;

    @Mock
    DateProvider dateProvider;

    @Mock
    User testUser;


    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();


    @Test
    public void testCreationNightlyTask() {

        NightlyTask nightlyTask = new NightlyTask(strategyRepositoryMock, portfolioRepositoryMock, assetRepositoryMock, dataRepositoryMock, userRepositoryMock, dataUpdaterMock);

        assertEquals(
                "class it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.NightlyTask",
                nightlyTask.getClass().toString());

    }

//    @Test
//    public void testNightlyTask() {
//
//        NightlyTask nightlyTask = new NightlyTask(strategyRepositoryMock, portfolioRepositoryMock, assetRepositoryMock, dataRepositoryMock, userRepositoryMock, dataUpdaterMock);
//
//        List<User> users = new ArrayList<>();
//        users.add(testUser);
//
//
//
//    }

}

package it.uiip.digitalgarage.roboadvice.tests;

import it.uiip.digitalgarage.roboadvice.Application;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater.Quandl.QuandlDataSource;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dateProvider.DateProvider;
import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.sql.Date;

import static org.junit.Assert.assertEquals;


/**
 * Created by Simone on 09/03/2017.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
                                TransactionalTestExecutionListener.class})
public class QuandlDataSourceTests {

    @Autowired
    private QuandlDataSource quandlDataSource;

    @Autowired
    private AssetRepository assetRepository;

    private static final Log LOGGER = LogFactory.getLog(Application.class);

    private Long assetId = 1L;
    private Asset asset;
    private Data data;
    private Date yesterday = new DateProvider().getYesterday();

    @Test
    public void testQuandlDataSource() {

        assertEquals(
                "class it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater.Quandl.QuandlDataSource",
                this.quandlDataSource.getClass().toString());

    }

    @Test
    public void testAssetSearch() {
        asset = assetRepository.findOne(assetId);
        assertEquals(
                assetId,
                asset.getId()
        );
    }

    @Test
    public void testGetData() {
//
//        asset = assetRepository.findOne(assetId);
//        data = quandlDataSource.getDailyData(
//                asset,
//                yesterday
//        );
//        if (data == null) {
//            LOGGER.debug("The test for Quandl asset " + asset + " can't be performed extensively for the date (" +
//                    yesterday + ")");
//            assertTrue(true);
//        } else {
//            assertEquals(
//                    yesterday,
//                    data.getDate()
//            );
//
//            assertEquals(
//                    "class java.math.BigDecimal",
//                    data.getValue().getClass().toString()
//            );
//        }

    }

}

package it.uiip.digitalgarage.roboadvice.test.unitTests;

import it.uiip.digitalgarage.roboadvice.Application;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater.IDataSource;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater.Quandl.QuandlDataSource;
import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.sql.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


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

    private Long assetId;
    private Asset asset;
    private List<Data> dataList;
    private Data data;
    private Date yesterday;

    @Before
    public void before(){

        assetId = 1L;
        yesterday = CustomDate.getToday().getYesterdaySql();
    }

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
    public void testGetAllDataFrom() {

        asset = assetRepository.findOne(assetId);

        try {
            dataList = quandlDataSource.getAllDataFrom(
                    asset,
                    yesterday
            );
        } catch (IDataSource.ConnectionException e) {
            assertTrue(false);
        }
        if (dataList == null) {
            LOGGER.debug("The test for Quandl asset " + asset + " can't be performed extensively for the date (" +
                    yesterday + ")");
            assertTrue(true);
        } else {

            for (Data curData : dataList) {

                assertEquals(
                        yesterday.toString(),
                        curData.getDate().toString()
                );

                assertEquals(
                        "class java.math.BigDecimal",
                        curData.getValue().getClass().toString()
                );

            }

        }

    }

}

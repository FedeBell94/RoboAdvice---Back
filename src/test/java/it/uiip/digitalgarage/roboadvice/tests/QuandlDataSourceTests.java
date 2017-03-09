package it.uiip.digitalgarage.roboadvice.tests;

import it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.dataUpdater.Quandl.QuandlDataSource;
import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.utils.Utils;

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

    private int assetId = 1;
    private Asset asset;
    private Data data;
    private Date yesterday = Utils.getYesterday();

    @Test
    public void testQuandlDataSource() {

        assertEquals(
                "class it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.dataUpdater.Quandl.QuandlDataSource",
                this.quandlDataSource.getClass().toString());

    }

    @Test
    public void testAssetSearch() {
        asset = assetRepository.findById(assetId);
        assertEquals(
                assetId,
                asset.getId()
        );
    }

    @Test
    public void testGetData() {

        asset = assetRepository.findById(assetId);
        data = quandlDataSource.getData(
                asset,
                yesterday
        );

        assertEquals(
                yesterday,
                data.getDate()
        );

        assertEquals(
                "class java.math.BigDecimal",
                data.getValue().getClass().toString()
        );
    }

}

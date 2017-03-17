package it.uiip.digitalgarage.roboadvice.tests;

import it.uiip.digitalgarage.roboadvice.businesslogic.controller.AssetClassRESTController;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.AssetClassHistoryDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.NightlyTask;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dateProvider.DateProvider;
import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.AssetClass;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetClassRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
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
import javax.validation.constraints.AssertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by Simone on 17/03/2017.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {PersistenceContext.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
public class AssetClassRESTControllerTests {

    @Mock
    private DataRepository dataRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private AssetClassRepository assetClassRepository;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private AssetClassRESTController assetClassRESTController;

    private DateProvider dateProvider;


    @Before
    public void before(){

        dateProvider = new DateProvider();

        assetClassRESTController = new AssetClassRESTController(dataRepository,assetRepository,assetClassRepository);

        List<AssetClass> assetClassList = new ArrayList<>();
        List<Asset> assetList = new ArrayList<>();
        List<Data> dataList = new ArrayList<>();

        AssetClass assetClass = AssetClass.builder().name("TestClass").id(1L).build();
        Asset asset = Asset.builder().id(1L).name("TestAsset").quandlKey("WIKI/JUNIT").quandlId(1).fixedPercentage(new BigDecimal("100")).build();
        Data data = Data.builder().date(dateProvider.getYesterday()).id(300L).value(new BigDecimal("900")).asset(asset).build();

        assetClassList.add(assetClass);
        assetList.add(asset);
        dataList.add(data);

        when(assetClassRepository.findAll()).thenReturn(assetClassList);
        when(assetRepository.findByAssetClass(any())).thenReturn(assetList);
        when(dataRepository.findByDateAfterAndAssetOrderByDateAsc(any(),any())).thenReturn(dataList);
        when(dataRepository.findTop1ByDateBeforeAndAssetOrderByDateDesc(any(),any())).thenReturn(data);


    }

    @Test
    public void testGetAssetClasses() {

        List<AssetClass> result = (List<AssetClass>) assetClassRESTController.getAssetClasses().getData();

        Boolean check = result.get(0).getName().equals("TestClass");

        assertTrue(check);

    }

    @Test
    public void testGetAssetClassHistory(){

        List<AssetClassHistoryDTO> result =( List<AssetClassHistoryDTO>) assetClassRESTController.getAssetClassHistory(1L,dateProvider.getYesterday().toLocalDate(),LocalDate.now()).getData();

        Boolean check = result.get(0).getDate().toString().equals(dateProvider.getYesterday().toLocalDate().toString());

        assertTrue(check);

    }

}

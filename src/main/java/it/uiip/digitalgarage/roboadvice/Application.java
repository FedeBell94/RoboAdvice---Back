package it.uiip.digitalgarage.roboadvice;

import com.jimmoores.quandl.*;
import it.uiip.digitalgarage.roboadvice.businesslogic.quandl.Quandl;
import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.AssetClass;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetClassRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.utils.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.List;


@SpringBootApplication
public class Application {


    //api key: ydS_4tVLnsPS_bxo3uvd
    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetClassRepository assetClassRepository;

    public ArrayList<Data> callQuandl(Asset asset, int startYear, int startMonth, int startDay) {

        QuandlSession session = QuandlSession.create("ydS_4tVLnsPS_bxo3uvd");
        @SuppressWarnings("deprecation")
        TabularResult tabularResultMulti = session.getDataSets(
                MultiDataSetRequest.Builder
                        .of(
                                QuandlCodeRequest.singleColumn(asset.getQuandlKey(), asset.getQuandlId())
                        )
                        .withStartDate(LocalDate.of(startYear, startMonth, startDay))
                        .withFrequency(Frequency.DAILY)
                        .build());
        ArrayList<Data> res = new ArrayList<>();
        for (final Row row : tabularResultMulti) {
            LocalDate ldate = row.getLocalDate("Date");
            Double value = row.getDouble(/*asset.getQuandlKey() + " - " + asset.getQuandlColumn()*/1);
            if (value != null) {
                Data d = Data.builder().date(DateTimeUtils.toSqlDate(ldate)).value(BigDecimal.valueOf(value)).asset(asset).build();
                res.add(d);
                Data dop = dataRepository.findByAssetAndDate(asset, DateTimeUtils.toSqlDate(ldate));
                if (dop == null) {
                    System.out.println("inserito!");
                    dataRepository.save(d);
                }

            }
        }
        return res;
    }

    public Data callDailyQuandl(Asset asset) {


        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date ddate = new java.sql.Date(cal.getTimeInMillis());


        QuandlSession session = QuandlSession.create("ydS_4tVLnsPS_bxo3uvd");
        /*switch (asset.getQuandlColumn()) {
            case "Open":
            case "Rate":
            case "24H Average":
            case "Adj. Close":{

                break;
            }
            case "Once/month":{

                break;
            }
        }*/

        System.out.println("Asking info for date: " + ddate.toLocalDate().getYear() + " - " + ddate.toLocalDate().getMonthValue() + " - " + ddate.toLocalDate().getDayOfMonth());
        @SuppressWarnings("deprecation")
        TabularResult tabularResultMulti = session.getDataSets(
                MultiDataSetRequest.Builder.of(
                        QuandlCodeRequest.singleColumn(asset.getQuandlKey(), asset.getQuandlId())
                        //QuandlCodeRequest.allColumns("DOE/RWTC")
                )
                        .withStartDate(LocalDate.of(ddate.toLocalDate().getYear(), ddate.toLocalDate().getMonthValue(), ddate.toLocalDate().getDayOfMonth()))
                        .build());
        System.out.println("Header definition: " + tabularResultMulti.getHeaderDefinition());

        for (final Row row : tabularResultMulti) {

            LocalDate date = row.getLocalDate("Date");
            Double value = row.getDouble(/*asset.getQuandlKey() + " - " + asset.getQuandlColumn()*/1);
            System.out.println("Value on date " + date + " was " + value);
            if (value != null) {
                Data d = Data.builder().date(new java.sql.Date(cal.getTimeInMillis()))
                        .value(BigDecimal.valueOf(value))
                        .asset(asset)
                        .build();


                Data dop = dataRepository.findByAssetAndDate(asset, new java.sql.Date(cal.getTimeInMillis()));
                if (dop == null) {
                    System.out.println("inserito!");
                    dataRepository.save(d);
                }
                return d;
            } else {
                System.out.println("Nothing found");
                return null;
            }
        }
        return null;
    }


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @PostConstruct
    void func() {
        //  Asset a = assetRepository.findOne(1);
        //callDailyQuandl(a);
        // callQuandl(a,2016,5,3);
        /*Iterator it = assetRepository.findAll().iterator();
        while (it.hasNext()) {
            Asset a = (Asset) it.next();

            callQuandl(a, 2012, 3, 2);


        }*/
    }


    @PostConstruct
    public void initDatabaseDefaultValues() {
        Logger.debug(Application.class, "Database default values checking and inserting");

        JSONParser jsonParser = new JSONParser();
        try {
            Reader defaultAssetClassReader = new FileReader(getClass().getClassLoader()
                    .getResource("DefaultAssetClass.json").getPath());
            Reader defaultAssetReader = new FileReader(getClass().getClassLoader()
                    .getResource("DefaultAsset.json").getPath());

            List<JSONObject> assetClassList = (List<JSONObject>) jsonParser.parse(defaultAssetClassReader);
            List<JSONObject> assetList = (List<JSONObject>) jsonParser.parse(defaultAssetReader);

            // Check if there is already something into the DB
            if (assetClassList.size() != assetClassRepository.count()) {
                insertDefaultAssetClasses(assetClassList);
            }

            // Check if there is already something into the DB
            if (assetList.size() != assetRepository.count()) {
                insertDefaultAssets(assetList);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(Application.class, "Failed to add default data in database.");
        }
    }

    private void insertDefaultAssetClasses(List<JSONObject> assetClassList) {
        Logger.debug(Application.class, "Writing default asset class in database");
        assetClassRepository.deleteAll();
        for (JSONObject curr : assetClassList) {
            assetClassRepository.save(AssetClass.builder()
                    .id(Integer.parseInt((String) curr.get("id")))
                    .name((String) curr.get("name"))
                    .build());
        }
    }

    private void insertDefaultAssets(List<JSONObject> assetList) {
        Logger.debug(Application.class, "Writing asset in database");
        assetRepository.deleteAll();
        for (JSONObject curr : assetList) {
            Asset a = Asset.builder()
                    .id(Integer.parseInt((String) curr.get("id")))
                    .assetClass(assetClassRepository.findOne(Integer.parseInt((String) curr.get("assetClassId"))))
                    .name((String) curr.get("name"))
                    .quandlKey((String) curr.get("quandlKey"))
                    .quandlId(Integer.parseInt((String) curr.get("quandlId")))
                    .quandlColumn((String) curr.get("quandlColumn"))
                    .fixedPercentage(new BigDecimal((String) curr.get("fixedPercentage")))
                    .build();
            assetRepository.save(a);
        }

    }
}

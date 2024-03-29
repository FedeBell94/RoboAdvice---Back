package it.uiip.digitalgarage.roboadvice;

import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.AssetClass;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetClassRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.service.adviceTask.AdviceTask;
import it.uiip.digitalgarage.roboadvice.service.dataUpdater.IDataUpdater;
import it.uiip.digitalgarage.roboadvice.service.forecastTask.DataForecastTask;
import it.uiip.digitalgarage.roboadvice.service.nightlyTask.INightlyTask;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import it.uiip.digitalgarage.roboadvice.utils.RoboAdviceConstant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.List;

@EnableScheduling
@SpringBootApplication
public class Application {

    private static final Log LOGGER = LogFactory.getLog(Application.class);

    private final AssetRepository assetRepository;
    private final AssetClassRepository assetClassRepository;

    private final INightlyTask nightlyTask;
    private final IDataUpdater dataUpdater;
    private final AdviceTask adviceTask;
    private final DataForecastTask dataForecastTask;

    @Autowired
    public Application(AssetRepository assetRepository, AssetClassRepository assetClassRepository,
                       INightlyTask nightlyTask, IDataUpdater dataUpdater, AdviceTask adviceTask,
                       DataForecastTask dataForecastTask) {
        this.assetRepository = assetRepository;
        this.assetClassRepository = assetClassRepository;
        this.nightlyTask = nightlyTask;
        this.dataUpdater = dataUpdater;
        this.adviceTask = adviceTask;
        this.dataForecastTask = dataForecastTask;
    }


    // Execution of night task
    @PostConstruct
    @Scheduled(cron = "0 0 4 * * TUE-SAT")
    //@Scheduled(cron = "*/1 * * * * *")
    public void executeNightTask() {
        LOGGER.debug("Night task started.");
        Long startTime = System.currentTimeMillis();

        try{
            nightlyTask.executeNightlyTask();
            adviceTask.initializeForecastData();
            dataForecastTask
                    .initializeForecastData(CustomDate.getToday().getDayFromLocalDate(RoboAdviceConstant.FORECAST_DAYS));
        } catch (INightlyTask.NightlyTaskFailedException e){
            LOGGER.error(e.getMessage());
        }

        Long endTime = System.currentTimeMillis();
        LOGGER.debug("Night task ended -> execution time " + (endTime - startTime) + "ms. ");
    }

    @PostConstruct
    public void initDatabaseDefaultValues() {
        LOGGER.debug("Database default values checking and inserting");

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
            LOGGER.error("Failed to add default data in database.");
        }

        try {
            dataUpdater.updateAssetData();
        } catch (IDataUpdater.DataUpdateException e) {
            LOGGER.error("Failed to download data of the assets");
        }
    }

    private void insertDefaultAssetClasses(List<JSONObject> assetClassList) {
        LOGGER.debug("Writing default asset class in database");
        assetClassRepository.deleteAll();
        for (JSONObject curr : assetClassList) {
            assetClassRepository.save(AssetClass.builder()
                    .id(Long.parseLong((String) curr.get("id")))
                    .name((String) curr.get("name"))
                    .build());
        }
    }

    private void insertDefaultAssets(List<JSONObject> assetList) {
        LOGGER.debug("Writing asset in database");
        assetRepository.deleteAll();
        for (JSONObject curr : assetList) {
            Asset a = Asset.builder()
                    .id(Long.parseLong((String) curr.get("id")))
                    .assetClass(assetClassRepository.findOne(Long.parseLong((String) curr.get("assetClassId"))))
                    .name((String) curr.get("name"))
                    .quandlKey((String) curr.get("quandlKey"))
                    .quandlId(Integer.parseInt((String) curr.get("quandlId")))
                    .quandlColumn((String) curr.get("quandlColumn"))
                    .fixedPercentage(new BigDecimal((String) curr.get("fixedPercentage")))
                    .build();
            assetRepository.save(a);
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

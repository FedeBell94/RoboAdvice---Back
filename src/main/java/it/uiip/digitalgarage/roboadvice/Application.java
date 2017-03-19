package it.uiip.digitalgarage.roboadvice;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.INightlyTask;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater.IDataUpdater;
import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.AssetClass;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetClassRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.LoggerFactory;
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

    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final AssetClassRepository assetClassRepository;

    private final INightlyTask nightlyTask;
    private final IDataUpdater dataUpdater;

    @Autowired
    public Application(final UserRepository userRepository, final AssetRepository assetRepository,
                       final AssetClassRepository assetClassRepository, final INightlyTask nightlyTask, final
                       IDataUpdater dataUpdater) {
        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
        this.assetClassRepository = assetClassRepository;
        this.nightlyTask = nightlyTask;
        this.dataUpdater = dataUpdater;
    }


    // Execution of night task
    //@Scheduled(cron = "0 0 10 * * TUE-SAT")
    @Scheduled(cron = "*/1 * * * * *")
    public void executeNightTask() {
        LOGGER.debug("Night task started.");
        Long startTime = System.currentTimeMillis();
        nightlyTask.executeNightlyTask(userRepository.findAll());
        Long endTime = System.currentTimeMillis();
        LOGGER.debug("Night task ended -> execution time " + (endTime - startTime) + "ms. ");
    }

    @PostConstruct
    public void initDatabaseDefaultValues() {
        // Logger initialization
        Logger logger = (Logger) LoggerFactory.getLogger("it.uiip.digitalgarage.roboadvice");
        logger.setLevel(Level.DEBUG);

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

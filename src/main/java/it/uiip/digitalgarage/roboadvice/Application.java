package it.uiip.digitalgarage.roboadvice;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.dateProvider.DateProvider;
import it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.IDailyTaskUpdate;
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

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.List;

@EnableScheduling
@SpringBootApplication
public class Application {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetClassRepository assetClassRepository;

    @Autowired
    private IDailyTaskUpdate dailyTask;


    private static final Log LOGGER = LogFactory.getLog(Application.class);

    // Execution of night task
    //@Scheduled(cron = "0 0 10 * * TUE-SAT")
    //@Scheduled(cron = "*/5 * * * * TUE-SAT")
    public void executeNightTask(){
        LOGGER.debug("Night task started.");
        Long startTime = System.currentTimeMillis();
        DateProvider dateProvider = new DateProvider();
        dailyTask.executeUpdateTask(dateProvider, userRepository.findAll());
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
    }

    private void insertDefaultAssetClasses(List<JSONObject> assetClassList) {
        LOGGER.debug("Writing default asset class in database");
        assetClassRepository.deleteAll();
        for (JSONObject curr : assetClassList) {
            assetClassRepository.save(AssetClass.builder()
                    .id(Integer.parseInt((String) curr.get("id")))
                    .name((String) curr.get("name"))
                    .build());
        }
    }

    private void insertDefaultAssets(List<JSONObject> assetList) {
        LOGGER.debug("Writing asset in database");
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

    @PostConstruct
    private void initLogger(){
        Logger logger = (Logger) LoggerFactory.getLogger("it.uiip.digitalgarage.roboadvice");
        logger.setLevel(Level.DEBUG);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

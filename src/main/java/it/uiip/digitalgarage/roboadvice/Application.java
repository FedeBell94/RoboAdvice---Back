package it.uiip.digitalgarage.roboadvice;

import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetClassRepository;
import it.uiip.digitalgarage.roboadvice.utils.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;


@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private AssetClassRepository assetClassRepository;

    @PostConstruct
    public void initDatabaseDefaultValues() throws Exception {
        Logger.debug(Application.class, "Database default values checking and inserting");
        //assetClassRepository.save(AssetClass.builder().name("prova").build());

        JSONParser jsonParser = new JSONParser();
        ClassLoader classLoader = getClass().getClassLoader();
        Reader defaultAssetClassReader = new FileReader(classLoader.getResource("DefaultAssetClass.json").getPath());
        try {
            List<JSONObject> assetClassList = (List<JSONObject>) jsonParser.parse(defaultAssetClassReader);

            for(JSONObject curr : assetClassList){
                System.out.println((String)curr.get("name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

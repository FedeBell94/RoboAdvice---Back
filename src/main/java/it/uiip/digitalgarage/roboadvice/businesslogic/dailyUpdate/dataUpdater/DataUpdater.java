package it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.dataUpdater;

import it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.dataUpdater.DateProvider.DateProvider;
import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SuppressWarnings("unused")
public class DataUpdater implements IDataUpdater {

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private AssetRepository assetRepository;


    @Autowired
    private IDataSource dataSource;

    private static final Log LOGGER = LogFactory.getLog(DataUpdater.class);

    @Override
    public void updateDailyData() {
        List<Asset> assets = assetRepository.findAll();
        for(Asset currAsset : assets){
            Data data = dataSource.getData(currAsset, new DateProvider().getYesterday());
            if(data != null){
                LOGGER.debug("Inserted in data : " + data.toString());
                dataRepository.save(data);
            }
        }
    }
}

package it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.dataUpdater;

import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import it.uiip.digitalgarage.roboadvice.utils.Logger;
import it.uiip.digitalgarage.roboadvice.utils.Utils;
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

    @Override
    public void updateDailyData() {
        List<Asset> assets = assetRepository.findAll();
        for(Asset currAsset : assets){
            Data data = dataSource.getData(currAsset, Utils.getYesterday());
            if(data != null){
                Logger.debug(DataUpdater.class, "Inserted in data : " + data.toString());
                dataRepository.save(data);
            }
        }
    }
}

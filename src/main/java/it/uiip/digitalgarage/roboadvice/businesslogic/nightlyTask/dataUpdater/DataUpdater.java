package it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater;

import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dateProvider.DateProvider;
import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataUpdater implements IDataUpdater {

    private static final Log LOGGER = LogFactory.getLog(DataUpdater.class);

    private final DataRepository dataRepository;
    private final AssetRepository assetRepository;

    private final IDataSource dataSource;

    @Autowired
    public DataUpdater(final DataRepository dataRepository, final AssetRepository assetRepository,
                       final IDataSource dataSource) {
        this.dataRepository = dataRepository;
        this.assetRepository = assetRepository;
        this.dataSource = dataSource;
    }


    @Override
    public void updateDailyData() {
        Iterable<Asset> assets = assetRepository.findAll();
        for (Asset currAsset : assets) {
            Data data = dataSource.getData(currAsset, new DateProvider().getYesterday());
            if (data != null) {
                LOGGER.debug("Inserted in data : " + data.toString());
                dataRepository.save(data);
            }
        }
    }
}

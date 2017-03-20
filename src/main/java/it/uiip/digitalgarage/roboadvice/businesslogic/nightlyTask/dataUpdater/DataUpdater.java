package it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater;

import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DataUpdater implements IDataUpdater {

    private static final Log LOGGER = LogFactory.getLog(DataUpdater.class);

    private static final String STARTING_DATA_DATE = "2012-01-01";

    private final DataRepository dataRepository;
    private final AssetRepository assetRepository;

    private final IDataSource dataSource;

    private CustomDate lastDataUpdate;

    @Autowired
    public DataUpdater(final DataRepository dataRepository, final AssetRepository assetRepository,
                       final IDataSource dataSource) {
        this.dataRepository = dataRepository;
        this.assetRepository = assetRepository;
        this.dataSource = dataSource;
    }

    @Override
    public void updateAssetData() throws DataUpdateException {
        Data data = dataRepository.findTop1ByOrderByDateDesc();
        if (((data == null) || (CustomDate.getToday().compareTo(data.getDate()) != 0)) &&
                (lastDataUpdate == null || lastDataUpdate.compareTo(CustomDate.getToday()) < 0)) {
            try {
                computeDataUpdate();
                lastDataUpdate = CustomDate.getToday();
            } catch (IDataSource.ConnectionException e) {
                LOGGER.error("Failed to update asset data:" + e.getMessage());
                throw new DataUpdateException("Failed to update asset data: " + e.getMessage());
            }
        } else {
            LOGGER.info("In data table everything is already up-to-date");
        }
    }

    private void computeDataUpdate() throws IDataSource.ConnectionException {
        Iterable<Asset> assets = assetRepository.findAll();
        List<Data> dataList = new ArrayList<>();
        for (Asset currAsset : assets) {
            Data data = dataRepository.findTop1ByAssetOrderByDateDesc(currAsset);
            CustomDate startDate;
            if (data == null) {
                startDate = new CustomDate(STARTING_DATA_DATE);
            } else {
                startDate = new CustomDate(data.getDate()).moveOneDayForward();
            }
            LOGGER.debug("Updating data from " + startDate);
            dataList.addAll(dataSource.getAllDataFrom(currAsset, startDate, CustomDate.getToday()));
        }
        dataRepository.save(dataList);
    }
}

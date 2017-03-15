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

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataUpdater implements IDataUpdater {

    private static final Log LOGGER = LogFactory.getLog(DataUpdater.class);

    private static final String STARTING_DATA_DATE = "2012-01-01";

    private final DataRepository dataRepository;
    private final AssetRepository assetRepository;

    private final IDataSource dataSource;

    private LocalDate lastDataUpdate;

    private DateProvider dateProvider = new DateProvider();

    @Autowired
    public DataUpdater(final DataRepository dataRepository, final AssetRepository assetRepository,
                       final IDataSource dataSource) {
        this.dataRepository = dataRepository;
        this.assetRepository = assetRepository;
        this.dataSource = dataSource;
    }

    @Override
    public void updateData() {
        Iterable<Asset> assets = assetRepository.findAll();
        List<Data> dataList = new ArrayList<>();

//        Data actualLastUpdate = dataRepository.findTop1ByOrderByDateDesc();
//        if((lastDataUpdate == null && actualLastUpdate.getDate().compareTo(dateProvider.getToday()) != 0)
//                || (lastDataUpdate != null && lastDataUpdate.compareTo(LocalDate.now()) != 0)) {
        if(lastDataUpdate == null || lastDataUpdate.compareTo(LocalDate.now()) != 0){
            for (Asset currAsset : assets) {
                Data data = dataRepository.findTop1ByAssetOrderByDateDesc(currAsset);
                Date startDate;
                if (data == null) {
                    startDate = Date.valueOf(STARTING_DATA_DATE);
                } else {
                    startDate = new Date(data.getDate().getTime() + 24 * 60 * 60 * 1000);
                }
                LOGGER.debug("Updating data from " + startDate);
                dataList.addAll(dataSource.getAllDataFrom(currAsset, startDate));

            }
            dataRepository.save(dataList);
            lastDataUpdate = LocalDate.now();
        } else{
            LOGGER.debug("Everything is already up-to-date");
        }
    }
}

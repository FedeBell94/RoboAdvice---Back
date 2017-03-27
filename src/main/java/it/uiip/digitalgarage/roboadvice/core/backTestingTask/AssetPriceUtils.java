package it.uiip.digitalgarage.roboadvice.core.backTestingTask;

import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class AssetPriceUtils {

    private final CustomDate currentDate;
    private final Map<Long, BigDecimal> currentPrices;
    private final Iterable<Asset> assets;
    private final DataRepository dataRepository;

    private Map<Long, Map<Date, BigDecimal>> dataMap;

    public AssetPriceUtils(Date startingDate, Iterable<Asset> assets, DataRepository dataRepository) {
        this.currentDate = new CustomDate(startingDate);
        this.currentPrices = new HashMap<>();
        this.assets = assets;
        this.dataRepository = dataRepository;
        this.initClass();
    }

    private void initClass() {
        for (Asset currAsset : assets) {
            Data data = dataRepository
                    .findTop1ByDateLessThanEqualAndAssetOrderByDateDesc(currentDate.getDateSql(), currAsset);
            currentPrices.put(data.getAsset().getId(), data.getValue());
        }

        dataMap = new HashMap<>();
        for (Asset currAsset : assets){
            Iterable<Data> currData = dataRepository.findByDateAfterAndAsset(currentDate.getDateSql(), currAsset);

            Map<Date, BigDecimal> currDataMap = new HashMap<>();
            for(Data d : currData){
                currDataMap.put(d.getDate(), d.getValue());
            }
            dataMap.put(currAsset.getId(), currDataMap);
        }
    }

    public void moveOneDayForward(){
        this.currentDate.moveOneDayForward();
    }

    public Map<Long, BigDecimal> getLatestPrices(){
        for(Asset currAsset : assets){
            Map<Date, BigDecimal> currAssetData = dataMap.get(currAsset.getId());
            BigDecimal currAssetPrice = currAssetData.get(currentDate.getDateSql());
            if(currAssetPrice != null){
                currentPrices.put(currAsset.getId(), currAssetPrice);
            }
        }
        return this.currentPrices;
    }
}

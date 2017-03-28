package it.uiip.digitalgarage.roboadvice.service.backTestingTask;

import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class used to manage the prices of the assets. This class gives the prices for the current assets in the day
 * passed, and for the days after accessing only few times the database, and speeding up the performance of the project.
 */
public class AssetPriceUtils {

    private final CustomDate currentDate;
    private final Map<Long, BigDecimal> currentPrices;
    private final Iterable<Asset> assets;
    private final DataRepository dataRepository;

    private Map<Long, Map<Date, BigDecimal>> dataMap;

    /**
     * Constructor of the class.
     *
     * @param startingDate   The date from where give the assets price.
     * @param assets         All the assets
     * @param dataRepository The unique instance of the data repository.
     * @param inputDataMap   This is a map containing the id of the asset as key, and a map containing the date and the
     *                       respective value as value. This parameter can also be null.
     */
    public AssetPriceUtils(Date startingDate, Iterable<Asset> assets, DataRepository dataRepository,
                           Map<Long, Map<Date, BigDecimal>> inputDataMap) {
        this.currentDate = new CustomDate(startingDate);
        this.currentPrices = new HashMap<>();
        this.assets = assets;
        this.dataRepository = dataRepository;
        this.initClass(inputDataMap);
    }

    /**
     * Initialize the class finding the required data to provide the latest prices.
     */
    private void initClass(Map<Long, Map<Date, BigDecimal>> inputDataMap) {
        for (Asset currAsset : assets) {
            Data data = dataRepository
                    .findTop1ByDateLessThanEqualAndAssetOrderByDateDesc(currentDate.getDateSql(), currAsset);
            currentPrices.put(data.getAsset().getId(), data.getValue());
        }

        if (inputDataMap != null) {
            this.dataMap = inputDataMap;
        } else {
            dataMap = new HashMap<>();
            for (Asset currAsset : assets) {
                Iterable<Data> currData = dataRepository.findByDateAfterAndAsset(currentDate.getDateSql(), currAsset);

                Map<Date, BigDecimal> currDataMap = new HashMap<>();
                for (Data d : currData) {
                    currDataMap.put(d.getDate(), d.getValue());
                }
                dataMap.put(currAsset.getId(), currDataMap);
            }
        }
    }

    /**
     * Move the logical date of the instance of this class one day forward.
     */
    public void moveOneDayForward() {
        this.currentDate.moveOneDayForward();
    }

    /**
     * Return the latest prices for the assets in the current logical day.
     *
     * @return A {@link Map} containing the id of the asset as key, and the price of the asset as value.
     */
    public Map<Long, BigDecimal> getLatestPrices() {
        for (Asset currAsset : assets) {
            Map<Date, BigDecimal> currAssetData = dataMap.get(currAsset.getId());
            BigDecimal currAssetPrice = currAssetData.get(currentDate.getDateSql());
            if (currAssetPrice != null) {
                currentPrices.put(currAsset.getId(), currAssetPrice);
            }
        }
        return this.currentPrices;
    }
}

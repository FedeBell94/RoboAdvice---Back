package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.AssetClassHistoryDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dateProvider.LiarDateProvider;
import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.AssetClass;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetClassRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "securedApi")
public class AssetClassRESTController {

    private static final Log LOGGER = LogFactory.getLog(AssetClassRESTController.class);

    private final DataRepository dataRepository;
    private final AssetRepository assetRepository;
    private final AssetClassRepository assetClassRepository;

    @Autowired
    public AssetClassRESTController(final DataRepository dataRepository, final AssetRepository assetRepository,
                                    final AssetClassRepository assetClassRepository) {
        this.dataRepository = dataRepository;
        this.assetRepository = assetRepository;
        this.assetClassRepository = assetClassRepository;
    }

    @RequestMapping(value = "/assetClass", method = RequestMethod.GET)
    public @ResponseBody AbstractResponse getAssetClasses() {
        Iterable<AssetClass> assetClasses = assetClassRepository.findAll();
        LOGGER.debug("Get all asset class API called.");
        return new SuccessResponse<>(assetClasses);
    }

    @RequestMapping(value = "/assetClassHistory", method = RequestMethod.GET)
    public @ResponseBody AbstractResponse getAssetClassHistory(@RequestParam Long assetClassId,
                                                               @RequestParam(required = false)
                                                               @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
                                                               @RequestParam(required = false)
                                                               @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {

        // Check for the date. If the date passed is null, the default is 500 days from today.
        Date startDate;
        Date endDate;
        Calendar calendar = Calendar.getInstance();
        if (from == null) {
            endDate = new Date(calendar.getTimeInMillis());
            calendar.add(Calendar.DATE, -500);
            startDate = new Date(calendar.getTimeInMillis());
        } else {
            startDate = Date.valueOf(from);
            if (to == null) {
                endDate = new Date(calendar.getTimeInMillis());
            } else {
                endDate = Date.valueOf(to);
            }
        }
        LiarDateProvider dateProvider = new LiarDateProvider(startDate.toString());

        // Retrieve the assets for this asset class
        List<Asset> assetsAssetClass = assetRepository.findByAssetClass(AssetClass.builder().id(assetClassId).build());
        List<List<Data>> allAssets = new ArrayList<>();
        for (Asset currAsset : assetsAssetClass) {
            List<Data> currAssetData =
                    dataRepository.findByDateAfterAndAssetOrderByDateAsc(dateProvider.getYesterday(), currAsset);
            if(currAssetData.isEmpty()){
                currAssetData.add(Data.builder().asset(currAsset).date(Date.valueOf("2000-01-01")).build());
            }
            allAssets.add(currAssetData);
        }

        // Retrieve the last value for the asset before today if not already present in the data
        Map<Long, Data> currentData = new HashMap<>();
        for (List<Data> dataList : allAssets) {
            if (!dataList.isEmpty() && dataList.get(0).getDate().compareTo(dateProvider.getToday()) == 0) {
                currentData.put(dataList.get(0).getAsset().getId(), dataList.get(0));
            } else {
                Data d = dataRepository.findTop1ByDateBeforeAndAssetOrderByDateDesc(dateProvider.getToday(),
                        dataList.get(0).getAsset());
                currentData.put(d.getAsset().getId(), d);
            }
        }

        Map<LocalDate, BigDecimal> computedData = new HashMap<>();
        while (dateProvider.getToday().compareTo(endDate) <= 0) {

            // Finds the assets of today
            for (List<Data> dataList : allAssets) {
                for (Data d : dataList) {
                    if (d.getDate().compareTo(dateProvider.getToday()) == 0) {
                        currentData.put(d.getAsset().getId(), d);
                    }
                }
            }

            BigDecimal wightedSum = BigDecimal.valueOf(0);
            for (Asset asset : assetsAssetClass) {
                Data d = currentData.get(asset.getId());
                BigDecimal actualValue = d.getValue().multiply(asset.getFixedPercentage())
                        .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
                wightedSum = wightedSum.add(actualValue);
            }

            computedData.put(dateProvider.getToday().toLocalDate(), wightedSum);
            dateProvider.goNextDay();
        }


        // Mapping the compute data in DTO object
        List<AssetClassHistoryDTO> returnData = new ArrayList<>(computedData.size());
        SortedSet<LocalDate> keys = new TreeSet<>(computedData.keySet());
        for (LocalDate key : keys) {
            BigDecimal value = computedData.get(key);
            returnData.add(AssetClassHistoryDTO.builder()
                    .date(Date.valueOf(key))
                    .value(value).build());
        }

        LOGGER.debug("Get asset class history called for asset id " + assetClassId);
        return new SuccessResponse<>(returnData);
    }

}

package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import it.uiip.digitalgarage.roboadvice.businesslogic.exception.BadRequestException;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.AssetClassDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.AssetClass;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetClassRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import it.uiip.digitalgarage.roboadvice.utils.RoboAdviceConstant;
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

/**
 * Class used to create all the API rest used to manage the {@link AssetClass}.
 */
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

    /**
     * This method returns all the {@link AssetClass}.
     *
     * @return All the {@link AssetClass} present in the database.
     */
    @RequestMapping(value = "/assetClass", method = RequestMethod.GET)
    public AbstractResponse getAssetClasses() {
        Iterable<AssetClass> assetClasses = assetClassRepository.findAll();
        LOGGER.debug("Get all asset class API called.");
        return new SuccessResponse<>(assetClasses);
    }

    /**
     * This method returns the history of the {@link AssetClass} passed as parameter. This parameter is required. This
     * method also provides two optionals parameters: from and to. Both are not necessaries. If from is not specified
     * this method returns a default number of days of history of the portfolio. Otherwise if from is specified it
     * returns all the history of the asset from the date 'from' to the date 'to' if present or to the current day if
     * not specified. From and To date are both excluded.
     * The format of date required is yyyy-MM-dd.
     *
     * @param assetClassId The {@link AssetClass} id which history is required.
     * @param from         Optional - The date in format yyyy-MM-dd from when the history of the asset class is
     *                     required.
     * @param to           Optional - The date in format yyyy-MM-dd to when the history of the asset class is required.
     *
     * @return An {@link AbstractResponse} containing a list of {@link AssetClassDTO} objects required.
     *
     * @throws BadRequestException Exception thrown in cas date 'from' is after date 'to'.
     */
    @RequestMapping(value = "/assetClassHistory", method = RequestMethod.GET)
    public AbstractResponse getAssetClassHistory(@RequestParam Long assetClassId,
                                                 @RequestParam(required = false)
                                                 @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
                                                 @RequestParam(required = false)
                                                 @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to)
            throws BadRequestException {
        if (from != null) {
            if (RoboAdviceConstant.STARTING_DATA.compareTo(from) > 0) {
                throw new BadRequestException(
                        "Bad request - invalid data: you could not go before " + RoboAdviceConstant.STARTING_DATA);
            }
            if (CustomDate.getToday().compareTo(from) <= 0) {
                throw new BadRequestException("Bad request - Date from must be before today.");
            }
            if (to != null && from.compareTo(to) >= 0) {
                throw new BadRequestException("Bad request - Date 'from' must be before date 'to'.");
            }
        }

        // Check for the date. If the date passed is null, the default is 500 days from today.
        Date startDate;
        Date endDate;
        Calendar calendar = Calendar.getInstance();
        if (from == null) {
            endDate = new Date(calendar.getTimeInMillis());
            calendar.add(Calendar.DATE, -500);
            startDate = new Date(calendar.getTimeInMillis());
        } else {
            startDate = new Date(Date.valueOf(from).getTime() + 24 * 60 * 60 * 1000);
            if (to == null) {
                endDate = new Date(calendar.getTimeInMillis());
            } else {
                endDate = Date.valueOf(to);
            }
        }
        CustomDate customDate = new CustomDate(startDate);

        // Retrieve the assets for this asset class
        List<Asset> assetsAssetClass = assetRepository.findByAssetClass(AssetClass.builder().id(assetClassId).build());

        List<List<Data>> allAssets = new ArrayList<>();
        for (Asset currAsset : assetsAssetClass) {
            List<Data> currAssetData =
                    dataRepository.findByDateAfterAndAssetOrderByDateAsc(customDate.getYesterdaySql(), currAsset);
            if (currAssetData.isEmpty()) {
                currAssetData.add(Data.builder().asset(currAsset).date(Date.valueOf("1900-01-01")).build());
            }
            allAssets.add(currAssetData);

        }

        // Retrieve the last value for the asset before today if not already present in the data
        Map<Long, Data> currentData = new HashMap<>();
        for (List<Data> dataList : allAssets) {
            if (!dataList.isEmpty() && dataList.get(0).getDate().compareTo(customDate.getDateSql()) == 0) {
                currentData.put(dataList.get(0).getAsset().getId(), dataList.get(0));
            } else {
                Data d = dataRepository.findTop1ByDateBeforeAndAssetOrderByDateDesc(customDate.getDateSql(),
                        dataList.get(0).getAsset());
                currentData.put(d.getAsset().getId(), d);
            }
        }

        Map<LocalDate, BigDecimal> computedData = new HashMap<>();
        while (customDate.compareTo(endDate) <= 0) {

            // Finds the assets of today
            for (List<Data> dataList : allAssets) {
                for (Data d : dataList) {
                    if (customDate.compareTo(d.getDate()) == 0) {
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

            computedData.put(customDate.getDateLocalDate(), wightedSum);
            customDate.moveOneDayForward();
        }


        // Mapping the compute data in DTO object
        List<AssetClassDTO> returnData = new ArrayList<>(computedData.size());
        SortedSet<LocalDate> keys = new TreeSet<>(computedData.keySet());
        for (LocalDate key : keys) {
            BigDecimal value = computedData.get(key);
            returnData.add(AssetClassDTO.builder()
                    .date(Date.valueOf(key))
                    .value(value).build());
        }

        LOGGER.debug("Get asset class history called for asset id " + assetClassId);
        return new SuccessResponse<>(returnData);
    }

}

package it.uiip.digitalgarage.roboadvice.core.forecastTask;

import it.uiip.digitalgarage.roboadvice.businesslogic.controller.ForecastRESTController;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.AssetClassDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.PortfolioDTO;
import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.AssetClass;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetClassRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataForecastTask {

    private static final Log LOGGER = LogFactory.getLog(ForecastRESTController.class);

    private final DataRepository dataRepository;
    private final AssetRepository assetRepository;
    private final AssetClassRepository assetClassRepository;

    private List<PortfolioDTO> computedForecast;

    @Autowired
    public DataForecastTask(DataRepository dataRepository, AssetRepository assetRepository,
                            AssetClassRepository assetClassRepository) {
        this.dataRepository = dataRepository;
        this.assetRepository = assetRepository;
        this.assetClassRepository = assetClassRepository;
    }

    public List<PortfolioDTO> getAssetClassForecast(IDataForecastComputation dataForecastComputation,
                                                    LocalDate to) {
        // TODO or check the date of the data (from nightly task last execution)
        if (computedForecast == null) {
            LOGGER.debug("Data forecast computation started");
            Long startTime = System.currentTimeMillis();

            Iterable<Asset> assets = assetRepository.findAll();
            Map<Long, Map<Date, BigDecimal>> forecasts = new HashMap<>();
            for (Asset currAsset : assets) {
                Iterable<Data> assetData = dataRepository.findByAssetOrderByDateDesc(currAsset);
                List<AssetClassDTO> computedData = dataForecastComputation.computeForecast(assetData, to);
                Map<Date, BigDecimal> assetClassValues;
                if (forecasts.get(currAsset.getAssetClass().getId()) == null) {
                    assetClassValues = new HashMap<>();
                    for (AssetClassDTO currElement : computedData) {
                        BigDecimal value = currElement.getValue()
                                .multiply(currAsset.getFixedPercentage())
                                .divide(BigDecimal.valueOf(100), BigDecimal.ROUND_HALF_UP)
                                .setScale(4, BigDecimal.ROUND_HALF_UP);
                        assetClassValues.put(currElement.getDate(), value);
                    }
                    forecasts.put(currAsset.getAssetClass().getId(), assetClassValues);
                } else {
                    assetClassValues = forecasts.get(currAsset.getAssetClass().getId());
                    for (AssetClassDTO currElement : computedData) {
                        BigDecimal value = assetClassValues.get(currElement.getDate())
                                .add(currElement.getValue()
                                        .multiply(currAsset.getFixedPercentage())
                                        .divide(BigDecimal.valueOf(100), BigDecimal.ROUND_HALF_UP)
                                        .setScale(4, BigDecimal.ROUND_HALF_UP));
                        assetClassValues.put(currElement.getDate(), value);
                    }
                }

            }

            // Conversion of the data in List of PortfolioDTO
            Iterable<AssetClass> assetClassList = assetClassRepository.findAll();
            computedForecast = new ArrayList<>();
            CustomDate customDate = CustomDate.getToday();
            while(customDate.moveOneDayForward().compareTo(to) <= 0) {
                for (AssetClass currAssetClass : assetClassList) {
                    BigDecimal value = forecasts.get(currAssetClass.getId()).get(customDate.getDateSql());
                    PortfolioDTO portfolioDTO = PortfolioDTO.builder()
                            .assetClassId(currAssetClass.getId())
                            .date(customDate.getDateSql())
                            .value(value)
                            .build();
                    computedForecast.add(portfolioDTO);
                }
            }

            Long endTime = System.currentTimeMillis();
            LOGGER.debug("Forecast computation ended -> execution time " + (endTime - startTime) + "ms.");
        }

        return computedForecast;
    }
}

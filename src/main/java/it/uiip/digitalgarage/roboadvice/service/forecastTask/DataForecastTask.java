package it.uiip.digitalgarage.roboadvice.service.forecastTask;

import it.uiip.digitalgarage.roboadvice.businesslogic.controller.ForecastRESTController;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.PortfolioDTO;
import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetClassRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import it.uiip.digitalgarage.roboadvice.service.dataUpdater.IDataUpdater;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataForecastTask {

    private static final Log LOGGER = LogFactory.getLog(ForecastRESTController.class);

    private final DataRepository dataRepository;
    private final AssetRepository assetRepository;
    private final AssetClassRepository assetClassRepository;
    private final IDataUpdater dataUpdater;
    private final IDataForecastComputation dataForecastComputation;

    private Map<Long, Map<Date, BigDecimal>> computedForecast;
    private CustomDate computedForecastDate;

    @Autowired
    public DataForecastTask(DataRepository dataRepository, AssetRepository assetRepository,
                            AssetClassRepository assetClassRepository, IDataUpdater dataUpdater,
                            IDataForecastComputation dataForecastComputation) {
        this.dataRepository = dataRepository;
        this.assetRepository = assetRepository;
        this.assetClassRepository = assetClassRepository;
        this.dataUpdater = dataUpdater;
        this.dataForecastComputation = dataForecastComputation;
    }

    public List<PortfolioDTO> getForecast(LocalDate to, User user) {
        if (computedForecast == null || computedForecastDate == null ||
                computedForecastDate.compareTo(dataUpdater.getLastComputationData()) < 0) {
            LOGGER.debug("Data forecast computation started");
            Long startTime = System.currentTimeMillis();

            updateForecastData(to);

            Long endTime = System.currentTimeMillis();
            LOGGER.debug("Forecast computation ended -> execution time " + (endTime - startTime) + "ms.");
        }


        return null;
    }

    private void updateForecastData(LocalDate to) {
        computedForecast = new HashMap<>();
        Iterable<Asset> assets = assetRepository.findAll();
        for (Asset currAsset : assets) {
            Iterable<Data> assetData = dataRepository.findByAssetOrderByDateDesc(currAsset);
            Map<Date, BigDecimal> currAssetForecast = dataForecastComputation.computeForecast(assetData, to);
            computedForecast.put(currAsset.getId(), currAssetForecast);
        }
    }
}

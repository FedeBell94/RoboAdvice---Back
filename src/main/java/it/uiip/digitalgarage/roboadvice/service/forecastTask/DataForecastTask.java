package it.uiip.digitalgarage.roboadvice.service.forecastTask;

import it.uiip.digitalgarage.roboadvice.businesslogic.controller.ForecastRESTController;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.PortfolioDTO;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetClassRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
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



            

            Long endTime = System.currentTimeMillis();
            LOGGER.debug("Forecast computation ended -> execution time " + (endTime - startTime) + "ms.");
        }

        return computedForecast;
    }
}

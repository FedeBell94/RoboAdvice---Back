package it.uiip.digitalgarage.roboadvice.service.forecastTask;

import it.uiip.digitalgarage.roboadvice.businesslogic.controller.ForecastRESTController;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.PortfolioDTO;
import it.uiip.digitalgarage.roboadvice.persistence.model.*;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.PortfolioRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.StrategyRepository;
import it.uiip.digitalgarage.roboadvice.service.CoreTask;
import it.uiip.digitalgarage.roboadvice.service.backTestingTask.AssetPriceUtils;
import it.uiip.digitalgarage.roboadvice.service.backTestingTask.PortfolioConversionUtil;
import it.uiip.digitalgarage.roboadvice.service.dataUpdater.IDataUpdater;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataForecastTask {

    private static final Log LOGGER = LogFactory.getLog(ForecastRESTController.class);

    private final DataRepository dataRepository;
    private final AssetRepository assetRepository;
    private final StrategyRepository strategyRepository;
    private final PortfolioRepository portfolioRepository;
    private final IDataUpdater dataUpdater;
    private final IDataForecastComputation dataForecastComputation;
    private final PortfolioConversionUtil portfolioConversion;

    private Map<Long, Map<Date, BigDecimal>> computedForecast;
    private CustomDate computedForecastDate;

    @Autowired
    public DataForecastTask(DataRepository dataRepository, AssetRepository assetRepository, IDataUpdater dataUpdater,
                            StrategyRepository strategyRepository, IDataForecastComputation dataForecastComputation,
                            PortfolioConversionUtil portfolioConversion, PortfolioRepository portfolioRepository) {
        this.dataRepository = dataRepository;
        this.assetRepository = assetRepository;
        this.strategyRepository = strategyRepository;
        this.dataUpdater = dataUpdater;
        this.dataForecastComputation = dataForecastComputation;
        this.portfolioConversion = portfolioConversion;
        this.portfolioRepository = portfolioRepository;
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

        return computePortfolio(to, user);
    }

    private void updateForecastData(LocalDate to) {
        computedForecast = new HashMap<>();
        Iterable<Asset> assets = assetRepository.findAll();
        for (Asset currAsset : assets) {
            Iterable<Data> assetData = dataRepository.findByAssetOrderByDateDesc(currAsset);
            Map<Date, BigDecimal> currAssetForecast = dataForecastComputation.computeForecast(assetData, to);
            computedForecast.put(currAsset.getId(), currAssetForecast);
        }
        computedForecastDate = CustomDate.getToday();
    }

    @SuppressWarnings("Duplicates")
    private List<PortfolioDTO> computePortfolio(LocalDate to, User user) {
        CustomDate customDate = CustomDate.getToday();
        List<PortfolioDTO> returnPortfolio = new ArrayList<>();
        Iterable<Asset> assets = assetRepository.findAll();

        List<Strategy> activeStrategy = strategyRepository.findByUserAndActiveTrue(user);
        List<Portfolio> lastPortfolio =
                portfolioRepository.findByUserAndDate(user, user.getLastPortfolioComputation());
        // prevents error if the nightly task has not worked during the last night
        CustomDate today = CustomDate.getToday();
        for(Portfolio p : lastPortfolio){
            p.setDate(today.getDateSql());
        }

        AssetPriceUtils assetPriceUtils =
                new AssetPriceUtils(customDate.getDayFromSql(1), assets, dataRepository, computedForecast);

        while (customDate.moveOneDayForward().compareTo(to) <= 0) {
            Map<Long, BigDecimal> latestAssetPrice = assetPriceUtils.getLatestPrices();
            assetPriceUtils.moveOneDayForward();
            lastPortfolio = CoreTask.executeTask(user, lastPortfolio, activeStrategy, latestAssetPrice, assets, null);
            returnPortfolio.addAll(portfolioConversion.convertPortfolio(lastPortfolio));
        }

        return returnPortfolio;
    }
}

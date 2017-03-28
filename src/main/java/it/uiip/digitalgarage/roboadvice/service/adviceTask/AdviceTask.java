package it.uiip.digitalgarage.roboadvice.service.adviceTask;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.AdviceDTO;
import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.AssetClass;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetClassRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import it.uiip.digitalgarage.roboadvice.service.dataUpdater.IDataUpdater;
import it.uiip.digitalgarage.roboadvice.service.forecastTask.IDataForecastComputation;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
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
public class AdviceTask {

    private static final Integer ADVICE_DAYS = 4;

    private final DataRepository dataRepository;
    private final AssetRepository assetRepository;
    private final IDataUpdater dataUpdater;
    private final AssetClassRepository assetClassRepository;
    private final IDataForecastComputation dataForecastComputation;

    private Map<Long, Map<Date, BigDecimal>> computedForecast;
    private CustomDate computedForecastDate;
    private Map<Long, BigDecimal> initialValues;
    private Map<Long, BigDecimal> endValues;

    @Autowired
    public AdviceTask(DataRepository dataRepository, AssetRepository assetRepository, IDataUpdater dataUpdater,
                      IDataForecastComputation dataForecastComputation, AssetClassRepository assetClassRepository) {
        this.dataRepository = dataRepository;
        this.assetRepository = assetRepository;
        this.dataUpdater = dataUpdater;
        this.dataForecastComputation = dataForecastComputation;
        this.assetClassRepository = assetClassRepository;
    }

    @SuppressWarnings("Duplicates")
    public List<AdviceDTO> getAdvice() {
        initializeForecastData();

        Iterable<AssetClass> assetClass = assetClassRepository.findAll();
        List<AdviceDTO> adviceList = new ArrayList<>();
        for(AssetClass currAssetClass : assetClass){
            BigDecimal endValue = endValues.get(currAssetClass.getId());
            BigDecimal initialValue = initialValues.get(currAssetClass.getId());
            BigDecimal difference = BigDecimal.ZERO;
            if(endValue != null){
                difference = endValue.subtract(initialValue);
            }

            AdviceDTO.Advice advice = null;
            switch(difference.compareTo(BigDecimal.ZERO)){
                case 0:
                    advice = AdviceDTO.Advice.MAINTAIN_ASSET;
                    break;
                case 1:
                    advice = AdviceDTO.Advice.BUY_ASSET;
                    break;
                case -1:
                    advice = AdviceDTO.Advice.SELL_ASSET;
                    break;
            }
            adviceList.add(AdviceDTO.builder().assetClassId(currAssetClass.getId()).advice(advice).build());
        }

        return adviceList;
    }

    public void initializeForecastData(){
        if (computedForecast == null || computedForecastDate == null ||
                computedForecastDate.compareTo(dataUpdater.getLastComputationData()) < 0) {
            CustomDate adviceDate = CustomDate.getToday().moveForward(ADVICE_DAYS);
            updateForecastData(adviceDate.getDayFromLocalDate(1));

            // Building the map containing the values for each asset class for tomorrow
            Date tomorrow = CustomDate.getToday().moveOneDayForward().getDateSql();
            Iterable<Asset> assets = assetRepository.findAll();
            initialValues = new HashMap<>();
            for(Asset currAsset : assets){
                BigDecimal forecastValue = computedForecast.get(currAsset.getId()).get(tomorrow);
                BigDecimal value = initialValues.get(currAsset.getAssetClass().getId());
                if(forecastValue != null) {
                    if (value == null) {
                        initialValues.put(currAsset.getAssetClass().getId(), forecastValue);
                    } else {
                        initialValues.put(currAsset.getAssetClass().getId(), value.add(forecastValue));
                    }
                }
            }

            // Building the map containing the values for each asset class for the advice final date
            endValues = new HashMap<>();
            for(Asset currAsset : assets){
                BigDecimal forecastValue = computedForecast.get(currAsset.getId()).get(adviceDate.getDateSql());
                BigDecimal value = endValues.get(currAsset.getAssetClass().getId());
                if(forecastValue != null) {
                    if (value == null) {
                        endValues.put(currAsset.getAssetClass().getId(), forecastValue);
                    } else {
                        endValues.put(currAsset.getAssetClass().getId(), value.add(forecastValue));
                    }
                }
            }
        }
    }

    private void updateForecastData(LocalDate adviceDate) {
        computedForecast = new HashMap<>();
        Iterable<Asset> assets = assetRepository.findAll();
        for (Asset currAsset : assets) {
            Iterable<Data> assetData = dataRepository.findByAssetOrderByDateDesc(currAsset);
            Map<Date, BigDecimal> currAssetForecast = dataForecastComputation.computeForecast(assetData, adviceDate);
            computedForecast.put(currAsset.getId(), currAssetForecast);
        }
        computedForecastDate = CustomDate.getToday();
    }

}

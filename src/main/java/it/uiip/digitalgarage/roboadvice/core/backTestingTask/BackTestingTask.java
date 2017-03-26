package it.uiip.digitalgarage.roboadvice.core.backTestingTask;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.PortfolioDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.StrategyDTO;
import it.uiip.digitalgarage.roboadvice.core.CoreTask;
import it.uiip.digitalgarage.roboadvice.persistence.model.*;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BackTestingTask {

    private final AssetRepository assetRepository;
    private final DataRepository dataRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public BackTestingTask(AssetRepository assetRepository, DataRepository dataRepository, ModelMapper modelMapper){
        this.assetRepository = assetRepository;
        this.dataRepository = dataRepository;
        this.modelMapper = modelMapper;
    }

    public List<PortfolioDTO> computeBackTesting(Date fromDate, List<StrategyDTO> strategy) {

        User user = User.builder()
                .registration(fromDate)
                .lastPortfolioComputation(fromDate)
                .build();

        List<Strategy> activeStrategy = new ArrayList<>(strategy.size());
        for (StrategyDTO currStrategy : strategy) {
            Strategy insertStrategy = Strategy.builder()
                    .assetClass(AssetClass.builder().id(currStrategy.getAssetClassId()).build())
                    .percentage(currStrategy.getPercentage())
                    .startingDate(fromDate)
                    .user(user)
                    .build();
            activeStrategy.add(insertStrategy);
        }

        Iterable<Asset> assets = assetRepository.findAll();
        CustomDate customDate = new CustomDate(fromDate);

        CustomDate today = CustomDate.getToday();
        List<Portfolio> lastPortfolio = new ArrayList<>();
        List<Portfolio> portfolioList = new ArrayList<>();
        while (customDate.moveOneDayForward().compareTo(today) <= 0) {
            Map<Long, BigDecimal> latestAssetPrice = getLatestAssetPrices(assets, customDate.getDateSql());
            lastPortfolio = CoreTask.executeTask(user, lastPortfolio, activeStrategy, latestAssetPrice, assets, null);
            portfolioList.addAll(lastPortfolio);
        }

        List<PortfolioDTO> returnListDTO = new ArrayList<>(portfolioList.size());
        for (Portfolio p : portfolioList) {
            returnListDTO.add(modelMapper.map(p, PortfolioDTO.class));
        }

        // TODO remove this!!!!
        long curAssetClassID = 0;
        java.util.Date curDate = new Date(Long.MIN_VALUE);
        List<PortfolioDTO> returnAggregatedListDTO = new ArrayList<>();

        for (int i = 0; i < returnListDTO.size(); i++) {
            if (returnListDTO.get(i).getDate().compareTo(curDate) == 0) {
                if (returnListDTO.get(i).getAssetClassId() == curAssetClassID) {
                    returnAggregatedListDTO.get(returnAggregatedListDTO.size() - 1).setValue(
                            returnAggregatedListDTO.get(returnAggregatedListDTO.size() - 1).getValue()
                                    .add(returnListDTO.get(i).getValue()));
                } else {
                    curAssetClassID = returnListDTO.get(i).getAssetClassId();
                    returnAggregatedListDTO.add(PortfolioDTO.builder().assetClassId(curAssetClassID).date(curDate)
                            .value(returnListDTO.get(i).getValue()).build());
                }
            } else {
                curDate = returnListDTO.get(i).getDate();
                curAssetClassID = returnListDTO.get(i).getAssetClassId();
                returnAggregatedListDTO.add(PortfolioDTO.builder().assetClassId(curAssetClassID).date(curDate)
                        .value(returnListDTO.get(i).getValue()).build());
            }
        }

        return returnAggregatedListDTO;
    }

    private Map<Long, BigDecimal> getLatestAssetPrices(final Iterable<Asset> assets, final Date date) {
        // TODO make it faster
        Map<Long, BigDecimal> latestPrices = new HashMap<>();
        for (Asset curr : assets) {
            Data data = dataRepository.findTop1ByDateLessThanEqualAndAssetOrderByDateDesc(date, curr);
            latestPrices.put(data.getAsset().getId(), data.getValue());
        }
        return latestPrices;
    }
}

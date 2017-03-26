package it.uiip.digitalgarage.roboadvice.core.demoTask;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.PortfolioDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.StrategyDTO;
import it.uiip.digitalgarage.roboadvice.core.CoreTask;
import it.uiip.digitalgarage.roboadvice.core.backTestingTask.AssetPriceUtils;
import it.uiip.digitalgarage.roboadvice.persistence.model.*;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DemoTask {

    private final AssetRepository assetRepository;
    private final DataRepository dataRepository;
    private final ModelMapper modelMapper;

    public DemoTask(AssetRepository assetRepository, DataRepository dataRepository, ModelMapper modelMapper){
        this.assetRepository = assetRepository;
        this.dataRepository = dataRepository;
        this.modelMapper = modelMapper;
    }

    public List<PortfolioDTO> computeDemo(CustomDate from, CustomDate to, List<StrategyDTO> strategy,
                                          BigDecimal worth) {
        User user = User.builder()
                .registration(from.getYesterdaySql())
                .lastPortfolioComputation(from.getDateSql())
                .build();

        List<Strategy> activeStrategy = new ArrayList<>(strategy.size());
        for (StrategyDTO currStrategy : strategy) {
            Strategy insertStrategy = Strategy.builder()
                    .assetClass(AssetClass.builder().id(currStrategy.getAssetClassId()).build())
                    .percentage(currStrategy.getPercentage())
                    .startingDate(from.getDateSql())
                    .user(user)
                    .build();
            activeStrategy.add(insertStrategy);
        }

        Iterable<Asset> assets = assetRepository.findAll();
        AssetPriceUtils assetPriceUtils = new AssetPriceUtils(from.getDateSql(), assets, dataRepository);
        Map<Long, BigDecimal> latestAssetPrice = assetPriceUtils.getLatestPrices();
        assetPriceUtils.moveOneDayForward();

        List<Portfolio> lastPortfolio = new ArrayList<>();
        lastPortfolio =
                CoreTask.executeTask(user, lastPortfolio, activeStrategy, latestAssetPrice, assets, worth);

        List<Portfolio> returnList = new ArrayList<>();
        while (from.moveOneDayForward().compareTo(to) < 0) {
            latestAssetPrice = assetPriceUtils.getLatestPrices();
            assetPriceUtils.moveOneDayForward();
            lastPortfolio = CoreTask.executeTask(user, lastPortfolio, activeStrategy, latestAssetPrice, assets, null);
            returnList.addAll(lastPortfolio);
        }

        List<PortfolioDTO> returnListDTO = new ArrayList<>(returnList.size());
        for (Portfolio p : returnList) {
            returnListDTO.add(modelMapper.map(p, PortfolioDTO.class));
        }

        long curAssetClassID = 0;
        java.util.Date curDate = null;
        List<PortfolioDTO> returnAggregatedListDTO = new ArrayList<>();

        // TODO remove this!!!
        for (int i = 0; i < returnListDTO.size(); i++) {
            if (returnListDTO.get(i).getDate() == curDate) {
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
}

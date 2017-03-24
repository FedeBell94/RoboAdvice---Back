package it.uiip.digitalgarage.roboadvice.core.demoTask;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.PortfolioDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.StrategyDTO;
import it.uiip.digitalgarage.roboadvice.core.CoreTask;
import it.uiip.digitalgarage.roboadvice.persistence.model.*;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoTask {

    public static List<PortfolioDTO> computeDemo(CustomDate from, CustomDate to, List<StrategyDTO> strategy,
                                          BigDecimal worth, AssetRepository assetRepository,
                                          DataRepository dataRepository, ModelMapper modelMapper) {
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
        Map<Long, BigDecimal> latestAssetPrice = getLatestAssetPrices(assets, from.getDateSql(), dataRepository);

        List<Portfolio> lastPortfolio = new ArrayList<>();
        lastPortfolio =
                CoreTask.executeTask(user, lastPortfolio, activeStrategy, latestAssetPrice, assets, worth);

        List<Portfolio> returnList = new ArrayList<>();
        while (from.moveOneDayForward().compareTo(to) < 0) {
            latestAssetPrice = getLatestAssetPrices(assets, from.getDateSql(), dataRepository);
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

    private static Map<Long, BigDecimal> getLatestAssetPrices(final Iterable<Asset> assets, final Date date, final
                                                       DataRepository dataRepository) {
        // TODO make a class utility that returns this
        // TODO make it faster
        Map<Long, BigDecimal> latestPrices = new HashMap<>();
        for (Asset curr : assets) {
            Data data = dataRepository.findTop1ByDateLessThanEqualAndAssetOrderByDateDesc(date, curr);
            latestPrices.put(data.getAsset().getId(), data.getValue());
        }
        return latestPrices;
    }

    private DemoTask() {
    }
}

package it.uiip.digitalgarage.roboadvice.service.backTestingTask;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.PortfolioDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.StrategyDTO;
import it.uiip.digitalgarage.roboadvice.service.CoreTask;
import it.uiip.digitalgarage.roboadvice.persistence.model.*;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BackTestingTask {

    private final AssetRepository assetRepository;
    private final DataRepository dataRepository;
    private final PortfolioConversionUtil portfolioConversion;

    @Autowired
    public BackTestingTask(AssetRepository assetRepository, DataRepository dataRepository,
                           PortfolioConversionUtil portfolioConversion) {
        this.assetRepository = assetRepository;
        this.dataRepository = dataRepository;
        this.portfolioConversion = portfolioConversion;
    }

    @SuppressWarnings("Duplicates")
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
        List<PortfolioDTO> returnPortfolio = new ArrayList<>();
        AssetPriceUtils assetPriceUtils =
                new AssetPriceUtils(customDate.getDayFromSql(1), assets, dataRepository, null);
        while (customDate.moveOneDayForward().compareTo(today) <= 0) {
            Map<Long, BigDecimal> latestAssetPrice = assetPriceUtils.getLatestPrices();
            assetPriceUtils.moveOneDayForward();
            lastPortfolio = CoreTask.executeTask(user, lastPortfolio, activeStrategy, latestAssetPrice, assets, null);
            returnPortfolio.addAll(portfolioConversion.convertPortfolio(lastPortfolio));
        }
        return returnPortfolio;
    }
}

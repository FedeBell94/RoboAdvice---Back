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

/**
 * Class used to perform the back-testing of the strategy of an User.
 */
@Service
public class BackTestingTask {

    private final AssetRepository assetRepository;
    private final DataRepository dataRepository;

    @Autowired
    public BackTestingTask(AssetRepository assetRepository, DataRepository dataRepository) {
        this.assetRepository = assetRepository;
        this.dataRepository = dataRepository;
    }

    /**
     * This class compute the back-testing of the strategy passed, from the date passed, to today.
     *
     * @param fromDate The date from where to start to compute the back-testing.
     * @param strategy The strategy to use to compute the back testing.
     *
     * @return The portfolio of the user computed with the strategy passed from the given day.
     */
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
            returnPortfolio.addAll(PortfolioConversionUtil.convertPortfolio(lastPortfolio));
        }
        return returnPortfolio;
    }
}

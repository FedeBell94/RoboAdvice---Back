package it.uiip.digitalgarage.roboadvice.service.demoTask;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.PortfolioDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.StrategyDTO;
import it.uiip.digitalgarage.roboadvice.service.CoreTask;
import it.uiip.digitalgarage.roboadvice.service.backTestingTask.AssetPriceUtils;
import it.uiip.digitalgarage.roboadvice.service.backTestingTask.PortfolioConversionUtil;
import it.uiip.digitalgarage.roboadvice.persistence.model.*;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class used to perform the demo of the system.
 */
@Service
public class DemoTask {

    private final AssetRepository assetRepository;
    private final DataRepository dataRepository;

    @Autowired
    public DemoTask(AssetRepository assetRepository, DataRepository dataRepository) {
        this.assetRepository = assetRepository;
        this.dataRepository = dataRepository;
    }

    /**
     * This method compute the demo of the strategy passed between the date passed, and with the given worth.
     *
     * @param from     The date from which compute the demo.
     * @param to       The date to which compute the demo.
     * @param strategy The strategy with compute the demo.
     * @param worth    The initial worth to start the demo.
     *
     * @return The portfolios computed as demo.
     */
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
        AssetPriceUtils assetPriceUtils = new AssetPriceUtils(from.getDateSql(), assets, dataRepository, null);
        Map<Long, BigDecimal> latestAssetPrice = assetPriceUtils.getLatestPrices();
        assetPriceUtils.moveOneDayForward();

        List<Portfolio> lastPortfolio = new ArrayList<>();
        lastPortfolio =
                CoreTask.executeTask(user, lastPortfolio, activeStrategy, latestAssetPrice, assets, worth);

        List<PortfolioDTO> returnPortfolio = new ArrayList<>();
        while (from.moveOneDayForward().compareTo(to) < 0) {
            latestAssetPrice = assetPriceUtils.getLatestPrices();
            assetPriceUtils.moveOneDayForward();
            lastPortfolio = CoreTask.executeTask(user, lastPortfolio, activeStrategy, latestAssetPrice, assets, null);
            returnPortfolio.addAll(PortfolioConversionUtil.convertPortfolio(lastPortfolio));
        }
        return returnPortfolio;
    }
}

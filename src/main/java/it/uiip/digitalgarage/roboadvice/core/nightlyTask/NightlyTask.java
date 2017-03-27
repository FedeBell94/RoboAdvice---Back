package it.uiip.digitalgarage.roboadvice.core.nightlyTask;

import it.uiip.digitalgarage.roboadvice.core.CoreTask;
import it.uiip.digitalgarage.roboadvice.core.dataUpdater.IDataUpdater;
import it.uiip.digitalgarage.roboadvice.persistence.model.*;
import it.uiip.digitalgarage.roboadvice.persistence.repository.*;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;

@Service
public class NightlyTask implements INightlyTask {

    private static final Log LOGGER = LogFactory.getLog(NightlyTask.class);

    private final StrategyRepository strategyRepository;
    private final PortfolioRepository portfolioRepository;
    private final AssetRepository assetRepository;
    private final DataRepository dataRepository;
    private final UserRepository userRepository;
    private final IDataUpdater dataUpdater;

    @Autowired
    public NightlyTask(final StrategyRepository strategyRepository, final PortfolioRepository portfolioRepository,
                       final AssetRepository assetRepository, final DataRepository dataRepository,
                       final UserRepository userRepository, final IDataUpdater dataUpdater) {
        this.strategyRepository = strategyRepository;
        this.portfolioRepository = portfolioRepository;
        this.assetRepository = assetRepository;
        this.dataRepository = dataRepository;
        this.userRepository = userRepository;
        this.dataUpdater = dataUpdater;
    }

    @Override
    public void executeNightlyTask() {

        try {
            dataUpdater.updateAssetData();
        } catch (IDataUpdater.DataUpdateException e) {
            LOGGER.error("Failed to execute nightly task due to an error in updating asset data.");
            return;
        }

        Iterable<User> users = userRepository.findAll();
        Iterable<Asset> assets = assetRepository.findAll();
        Map<Long, BigDecimal> latestAssetPrice = getLatestAssetPrices(assets, CustomDate.getToday().getDateSql());

        List<Portfolio> savePortfolioList = new LinkedList<>();
        for (User currUser : users) {
            // Check if the user is already up to date
            if (CustomDate.getToday().compareTo(currUser.getLastPortfolioComputation()) == 0) {
                continue;
            }

            List<Portfolio> lastPortfolio =
                    portfolioRepository.findByUserAndDate(currUser, currUser.getLastPortfolioComputation());

            if (currUser.getLastPortfolioComputation().compareTo(CustomDate.getToday().getYesterdaySql()) != 0) {
                savePortfolioList.addAll(portfolioRecovery(currUser, lastPortfolio, assets));
            }

            List<Strategy> activeStrategy = strategyRepository.findByUserAndActiveTrue(currUser);

            savePortfolioList
                    .addAll(CoreTask
                            .executeTask(currUser, lastPortfolio, activeStrategy, latestAssetPrice, assets, null));

            currUser.setLastPortfolioComputation(CustomDate.getToday().getDateSql());
            currUser.setLastStrategyComputed(activeStrategy.get(0).getStartingDate());
            userRepository.save(currUser);
        }
        portfolioRepository.save(savePortfolioList);
    }

    private List<Portfolio> portfolioRecovery(User user, List<Portfolio> lastPortfolio, Iterable<Asset> assets) {
        List<Strategy> activeStrategy;
        if (user.getLastStrategyComputed() == null) {
            activeStrategy = strategyRepository.findByUserAndActiveTrue(user);
        } else {
            activeStrategy = strategyRepository.findByUserAndStartingDate(user, user.getLastStrategyComputed());
        }

        List<Portfolio> returnList = new LinkedList<>();
        do {
            Date date = new CustomDate(user.getLastPortfolioComputation()).moveOneDayForward().getDateSql();
            Map<Long, BigDecimal> assetPrice = getLatestAssetPrices(assets, date);

            // TODO do NOT change the caller lastPortfolio!!!!!!!
            List<Portfolio> tmp = CoreTask.executeTask(user, lastPortfolio, activeStrategy, assetPrice, assets, null);
            lastPortfolio.clear();
            lastPortfolio.addAll(tmp);
            returnList.addAll(lastPortfolio);
            user.setLastPortfolioComputation(date);
        } while (lastPortfolio.get(0).getDate().compareTo(CustomDate.getToday().getYesterdaySql()) != 0);

        user.setLastPortfolioComputation(CustomDate.getToday().getYesterdaySql());
        Date date = strategyRepository.findByUserAndActiveTrue(user).get(0).getStartingDate();
        user.setLastStrategyComputed(date);
        return returnList;
    }

    private Map<Long, BigDecimal> getLatestAssetPrices(final Iterable<Asset> assets, final Date date) {
        Map<Long, BigDecimal> latestPrices = new HashMap<>();
        for (Asset curr : assets) {
            Data data = dataRepository.findTop1ByDateLessThanEqualAndAssetOrderByDateDesc(date, curr);
            latestPrices.put(data.getAsset().getId(), data.getValue());
        }
        return latestPrices;
    }
}

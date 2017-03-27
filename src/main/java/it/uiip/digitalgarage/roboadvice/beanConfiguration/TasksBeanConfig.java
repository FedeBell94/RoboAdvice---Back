package it.uiip.digitalgarage.roboadvice.beanConfiguration;

import it.uiip.digitalgarage.roboadvice.core.dataUpdater.DataUpdater;
import it.uiip.digitalgarage.roboadvice.core.dataUpdater.IDataSource;
import it.uiip.digitalgarage.roboadvice.core.dataUpdater.IDataUpdater;
import it.uiip.digitalgarage.roboadvice.core.dataUpdater.quandl.QuandlDataSource;
import it.uiip.digitalgarage.roboadvice.core.nightlyTask.INightlyTask;
import it.uiip.digitalgarage.roboadvice.core.nightlyTask.NightlyTask;
import it.uiip.digitalgarage.roboadvice.persistence.repository.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("it.uiip.digitalgarage.roboadvice.persistence.repository")
public class TasksBeanConfig {

    @Bean
    public IDataSource dataSource() {
        return new QuandlDataSource();
    }

    @Bean
    public IDataUpdater dataUpdater(DataRepository dataRepository, AssetRepository assetRepository,
                                    IDataSource dataSource) {
        return new DataUpdater(dataRepository, assetRepository, dataSource);
    }

    @Bean
    public INightlyTask nightlyTask(StrategyRepository strategyRepository, PortfolioRepository portfolioRepository,
                                    AssetRepository assetRepository, DataRepository dataRepository,
                                    UserRepository userRepository, IDataUpdater dataUpdater) {
        return new NightlyTask(strategyRepository, portfolioRepository, assetRepository, dataRepository, userRepository,
                dataUpdater);
    }

}



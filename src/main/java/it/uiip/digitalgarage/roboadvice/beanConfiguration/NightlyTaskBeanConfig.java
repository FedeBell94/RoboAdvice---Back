package it.uiip.digitalgarage.roboadvice.beanConfiguration;

import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.INightlyTask;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.NightlyTask;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater.DataUpdater;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater.IDataSource;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater.IDataUpdater;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater.Quandl.QuandlDataSource;
import it.uiip.digitalgarage.roboadvice.persistence.repository.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static it.uiip.digitalgarage.roboadvice.beanConfiguration.NightlyTaskBeanConfig.COMPONENT_SCAN_PATH;
import static it.uiip.digitalgarage.roboadvice.beanConfiguration.NightlyTaskBeanConfig.JPA_REPOSITORY_PATH;

@Configuration
@ComponentScan(COMPONENT_SCAN_PATH)
@EnableJpaRepositories(JPA_REPOSITORY_PATH)
public class NightlyTaskBeanConfig {

    static final String COMPONENT_SCAN_PATH = "it.uiip.digitalgarage.roboadvice.nightlyTask";
    static final String JPA_REPOSITORY_PATH = "it.uiip.digitalgarage.roboadvice.persistence.repository";

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



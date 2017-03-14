package it.uiip.digitalgarage.roboadvice.beanConfiguration;

import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.INightlyTask;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.NightlyTask;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater.DataUpdater;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater.IDataSource;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater.IDataUpdater;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater.Quandl.QuandlDataSource;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.PortfolioRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.StrategyRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan("it.uiip.digitalgarage.roboadvice.nightlyTask")
@EnableJpaRepositories("it.uiip.digitalgarage.roboadvice.persistence.repository")
public class NightlyTaskBeanConfig {


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
                                    IDataUpdater dataUpdater) {
        return new NightlyTask(strategyRepository, portfolioRepository, assetRepository, dataRepository, dataUpdater);
    }

}



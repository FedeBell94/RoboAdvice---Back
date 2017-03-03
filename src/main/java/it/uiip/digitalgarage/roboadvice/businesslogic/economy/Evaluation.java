package it.uiip.digitalgarage.roboadvice.businesslogic.economy;

import it.uiip.digitalgarage.roboadvice.Application;
import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;

import java.math.BigDecimal;

/**
 * Created by Simone on 03/03/2017.
 */
public class Evaluation {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private DataRepository dataRepository;

    public BigDecimal evaluatePortfolio(User u){
        //portfolioRepository.findByUserAndDate(u,);
        return null;
    }

    public BigDecimal getLastValue(Asset asset){

        Data data = dataRepository.findFirst1ByAssetOrderByDateDesc(asset);

        return data.getValue();
    }

    public static void main(String[] args) {

    }

}

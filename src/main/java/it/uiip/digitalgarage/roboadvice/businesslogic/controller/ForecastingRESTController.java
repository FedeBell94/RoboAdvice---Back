package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.PortfolioDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.PortfolioRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by Simone on 22/03/2017.
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "securedApi")
public class ForecastingRESTController {

    private static final Log LOGGER = LogFactory.getLog(PortfolioRepository.class);

    private final DataRepository dataRepository;
    private final AssetRepository assetRepository;

    @Autowired
    public ForecastingRESTController(final DataRepository dataRepository, final AssetRepository assetRepository) {
        this.dataRepository = dataRepository;
        this.assetRepository = assetRepository;
    }

    @RequestMapping(value = "/forecast", method = RequestMethod.GET)
    public AbstractResponse requestForecast() {

        //6 months prediction data

        return new SuccessResponse<>(true);
    }

}

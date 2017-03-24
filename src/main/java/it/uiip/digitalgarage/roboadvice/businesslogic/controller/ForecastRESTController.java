package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "securedApi")
public class ForecastRESTController {

    private static final Log LOGGER = LogFactory.getLog(ForecastRESTController.class);

    private final DataRepository dataRepository;
    private final AssetRepository assetRepository;

    @Autowired
    public ForecastRESTController(final DataRepository dataRepository, final AssetRepository assetRepository) {
        this.dataRepository = dataRepository;
        this.assetRepository = assetRepository;
    }

    @RequestMapping(value = "/forecast", method = RequestMethod.GET)
    public AbstractResponse requestForecast() {

        //6 months prediction data

        return new SuccessResponse<>(true);
    }

}

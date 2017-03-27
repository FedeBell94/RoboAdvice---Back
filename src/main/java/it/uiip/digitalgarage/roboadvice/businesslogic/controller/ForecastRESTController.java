package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.PortfolioDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.persistence.repository.UserRepository;
import it.uiip.digitalgarage.roboadvice.service.forecastTask.DataForecastTask;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import it.uiip.digitalgarage.roboadvice.utils.RoboAdviceConstant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Class used to create all the API rest used to manage the forecast utilities.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "securedApi")
public class ForecastRESTController {

    private static final Log LOGGER = LogFactory.getLog(ForecastRESTController.class);

    private final DataForecastTask dataForecastTask;
    private final UserRepository userRepository;

    @Autowired
    public ForecastRESTController(DataForecastTask dataForecastTask, UserRepository userRepository) {
        this.dataForecastTask = dataForecastTask;
        this.userRepository = userRepository;
    }

    /**
     * This rest API use the designed service to compute the forecast for the user for the next month.
     *
     * @param authentication Represents the authentication token of an authenticated request.
     *
     * @return A {@link SuccessResponse} containing the list of {@link PortfolioDTO} representing the forecast of the
     * data.
     */
    @RequestMapping(value = "/forecast", method = RequestMethod.GET)
    public AbstractResponse requestForecast(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());

        LocalDate previsionDateTo = CustomDate.getToday().getDayFromLocalDate(RoboAdviceConstant.FORECAST_DAYS);
        List<PortfolioDTO> forecastData = dataForecastTask.getForecast(previsionDateTo, user);

        LOGGER.debug("Forecast rest API called.");
        return new SuccessResponse<>(forecastData);
    }

}

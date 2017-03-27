package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.BackTestingDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.PortfolioDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.StrategyDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.ErrorResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.service.backTestingTask.BackTestingTask;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.persistence.repository.PortfolioRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.UserRepository;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "securedApi")
public class PortfolioRESTController {

    private static final Log LOGGER = LogFactory.getLog(PortfolioRESTController.class);

    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final BackTestingTask backTestingTask;

    @Autowired
    public PortfolioRESTController(UserRepository userRepository, PortfolioRepository portfolioRepository,
                                   BackTestingTask backTestingTask) {
        this.userRepository = userRepository;
        this.portfolioRepository = portfolioRepository;
        this.backTestingTask = backTestingTask;
    }

    /**
     * This method returns the portfolio history for the user which called the method. The parameter from is optional.
     * If it is not specified this method returns all the portfolio history of the user from the first day of
     * registration to the last one computed. Otherwise this method returns all the portfolio history from the 'from'
     * date to the last one computed.
     * The required format for the date is yyyy-MM-dd.
     *
     * @param authentication Represents the authentication token of an authenticated request.
     * @param from           Optional - The date from when the portfolio history is needed. The required format id
     *                       yyyy-MM-dd.
     *
     * @return An {@link AbstractResponse} containing the list of {@link PortfolioDTO} requested.
     */
    @RequestMapping(value = "/portfolio", method = RequestMethod.GET)
    public AbstractResponse requestMyData(Authentication authentication,
                                          @RequestParam(required = false)
                                          @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from) {
        User user = userRepository.findByUsername(authentication.getName());

        Date fromDate = from == null ? user.getRegistration() : Date.valueOf(from);
        List<PortfolioDTO> portfolio = portfolioRepository.findPortfolioHistory(user, fromDate);
        LOGGER.debug("User: " + user.getUsername() + " - get portfolio called.");
        return new SuccessResponse<>(portfolio);
    }

    @RequestMapping(value = "/backTesting", method = RequestMethod.POST)
    public AbstractResponse backTesting(@RequestBody BackTestingDTO backtestingDTO) {

        if (backtestingDTO.getStrategy() == null) {
            return new ErrorResponse("Missing parameter: STRATEGY.");
        }

        Date fromDate = backtestingDTO.getFrom() == null ? CustomDate.getToday().getDayFromSql(-500) :
                backtestingDTO.getFrom();
        List<StrategyDTO> strategy = backtestingDTO.getStrategy();

        List<PortfolioDTO> computedList = backTestingTask.computeBackTesting(fromDate, strategy);

        LOGGER.debug("Back-test called from date " + backtestingDTO.getFrom());
        return new SuccessResponse<>(computedList);
    }

}

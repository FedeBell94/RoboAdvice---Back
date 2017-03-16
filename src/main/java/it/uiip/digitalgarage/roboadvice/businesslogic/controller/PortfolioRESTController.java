package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.PortfolioDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
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

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "securedApi")
public class PortfolioRESTController {

    private static final Log LOGGER = LogFactory.getLog(PortfolioRepository.class);

    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;

    @Autowired
    public PortfolioRESTController(final UserRepository userRepository, final PortfolioRepository portfolioRepository) {
        this.userRepository = userRepository;
        this.portfolioRepository = portfolioRepository;
    }


    @RequestMapping(value = "/portfolio", method = RequestMethod.GET)
    public @ResponseBody AbstractResponse requestMyData(Authentication authentication,
                                                        @RequestParam(required = false)
                                                        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from) {
        User user = userRepository.findByUsername(authentication.getName());

        Date fromDate = from == null ? user.getRegistration() : Date.valueOf(from);
        List<PortfolioDTO> portfolio = portfolioRepository.findPortfolioHistory(user, fromDate);
        return new SuccessResponse<>(portfolio);

    }
}

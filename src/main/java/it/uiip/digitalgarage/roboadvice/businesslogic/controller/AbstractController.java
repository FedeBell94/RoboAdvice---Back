package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.ErrorResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.ExchangeError;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.persistence.repository.*;
import it.uiip.digitalgarage.roboadvice.utils.AuthProvider;
import it.uiip.digitalgarage.roboadvice.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

public class AbstractController {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected StrategyRepository strategyRepository;

    @Autowired
    protected AssetClassRepository assetClassRepository;

    @Autowired
    protected AssetRepository assetRepository;

    @Autowired
    protected DataRepository dataRepository;

    @Autowired
    protected PortfolioRepository portfolioRepository;

    synchronized protected AbstractResponse executeSafeTask(HttpServletRequest request, TaskLogic task) {
        String userToken = request.getHeader("User-Token");
        Integer userId = AuthProvider.getInstance().checkToken(userToken);
        if(userToken == null || userId == null){
            Logger.debug(StrategyRESTController.class, "Request made with wrong user token.");
            return new ErrorResponse(ExchangeError.SECURITY_ERROR);
        }

        User currUser = userRepository.findOne(userId);
        return task.executeTask(currUser);
    }

    @FunctionalInterface
    protected interface TaskLogic {
        AbstractResponse executeTask(User user);
    }
}

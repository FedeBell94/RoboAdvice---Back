package it.uiip.digitalgarage.roboadvice.businesslogic.controller;


import it.uiip.digitalgarage.roboadvice.businesslogic.model.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.ErrorResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.ExchangeError;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.AssetClass;
import it.uiip.digitalgarage.roboadvice.persistence.model.Strategy;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetClassRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.StrategyRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.UserRepository;
import it.uiip.digitalgarage.roboadvice.utils.AuthProvider;
import it.uiip.digitalgarage.roboadvice.utils.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

@RestController
@SuppressWarnings("unused")
public class StrategyRESTController {

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private AssetClassRepository assetClassRepository;

    @Autowired
    private UserRepository userRepository;

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/updateStrategy", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse updateStrategy(@RequestBody JSONObject strategyInput, HttpServletRequest request) {
        String userToken = request.getHeader("User-Token");
        Integer userId = AuthProvider.getInstance().checkToken(userToken);
        if(userToken == null || userId == null){
            Logger.debug(StrategyRESTController.class, "Request with wrong user token.");
            return new ErrorResponse(ExchangeError.SECURITY_ERROR);
        }
        User user = userRepository.findOne(userId);

        // Disable the previous active strategy
        List<Strategy> previousActive = strategyRepository.findByUserAndActiveTrue(user);
        for(Strategy curr: previousActive){
            strategyRepository.disactiveStrategy(curr.getId());
        }

        // Insert new strategy
        Iterable<AssetClass> it = assetClassRepository.findAll();
        for(AssetClass curr : it){
            BigDecimal percentage = new BigDecimal((String) strategyInput.get(String.valueOf(curr.getId())));
            Strategy newStrategy = Strategy.builder()
                    .user(user)
                    .assetClass(curr)
                    .percentage(percentage)
                    .active(true)
                    .startingDate(new Date(Calendar.getInstance().getTimeInMillis())).build();
            strategyRepository.save(newStrategy);
            Logger.debug(StrategyRESTController.class, "Insert strategy " + newStrategy);
        }
        return new SuccessResponse<>(null);
    }
}

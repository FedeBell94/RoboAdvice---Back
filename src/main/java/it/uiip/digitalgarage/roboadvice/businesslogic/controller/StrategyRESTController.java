package it.uiip.digitalgarage.roboadvice.businesslogic.controller;


import it.uiip.digitalgarage.roboadvice.businesslogic.model.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.ErrorResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.ExchangeError;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.persistence.model.AssetClass;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetClassRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.StrategyRepository;
import it.uiip.digitalgarage.roboadvice.utils.AuthProvider;
import it.uiip.digitalgarage.roboadvice.utils.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;

@RestController
@SuppressWarnings("unused")
public class StrategyRESTController {

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private AssetClassRepository assetClassRepository;

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/updateStrategy", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse updateStrategy(@RequestBody JSONObject strategyInput, HttpServletRequest request) {
        String userToken = request.getHeader("User-Token");
        Integer userId = AuthProvider.getInstance().checkToken(userToken);
        if(userToken == null || userId == null){
            Logger.debug(StrategyRESTController.class, "Request with wrong user token.");
            return new ErrorResponse(ExchangeError.SECURITY_ERROR);
        }


        Iterable<AssetClass> it = assetClassRepository.findAll();
        for(AssetClass curr : it){
            Logger.error(StrategyRESTController.class, (String)strategyInput.get(String.valueOf(curr.getId())));
            // TODO persistence!!!
        }
        return new SuccessResponse<>(null);
    }
}

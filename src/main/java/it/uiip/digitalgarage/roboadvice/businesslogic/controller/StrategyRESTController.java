package it.uiip.digitalgarage.roboadvice.businesslogic.controller;


import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.StrategyDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.persistence.model.AssetClass;
import it.uiip.digitalgarage.roboadvice.persistence.model.Strategy;
import it.uiip.digitalgarage.roboadvice.utils.Logger;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

@RestController
@SuppressWarnings("unused")
public class StrategyRESTController extends AbstractController {

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/strategy", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse updateStrategy(@RequestBody List<StrategyDTO> strategyInput,
                                                         HttpServletRequest request) {

        return super.executeSafeTask(request, (user) ->{
            // Disable the previous active strategy
            List<Strategy> previousActive = strategyRepository.findByUserAndActiveTrue(user);
            for(Strategy curr: previousActive){
                strategyRepository.disactiveStrategy(curr.getId());
            }

            // Insert new strategy
            for(StrategyDTO curr: strategyInput){
                AssetClass assetClass = assetClassRepository.findOne(curr.getAssetClassId());
                Strategy newStrategy = Strategy.builder()
                        .user(user)
                        .assetClass(assetClass)
                        .percentage(curr.getPercentage())
                        .active(true)
                        .startingDate(new Date(Calendar.getInstance().getTimeInMillis())).build();
                strategyRepository.save(newStrategy);
                Logger.debug(StrategyRESTController.class, "Inserted strategy " + newStrategy);
            }
            return new SuccessResponse<>(null);
        });
    }


    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/strategy", method = RequestMethod.GET)
    public @ResponseBody AbstractResponse getStrategy(HttpServletRequest request) {

        return super.executeSafeTask(request, (user) ->{
            // Retrieve the strategies
            List<Strategy> strategy = strategyRepository.findByUserAndActiveTrue(user);
            List<StrategyDTO> strategyDTO = new LinkedList<>();
            for(Strategy curr : strategy){
                strategyDTO.add(new StrategyDTO(curr));
            }
            Logger.debug(StrategyRESTController.class, "Get strategy API called.");
            return new SuccessResponse<>(strategyDTO);
        });
    }
}

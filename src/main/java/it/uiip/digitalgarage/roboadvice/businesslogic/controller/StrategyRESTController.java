package it.uiip.digitalgarage.roboadvice.businesslogic.controller;


import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.StrategyDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.persistence.model.AssetClass;
import it.uiip.digitalgarage.roboadvice.persistence.model.Strategy;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetClassRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.StrategyRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Class used to create all the API rest used to manage the {@link Strategy}.
 */
@RestController
@RequestMapping(value = "securedApi")
public class StrategyRESTController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private AssetClassRepository assetClassRepository;

    private static final Log LOGGER = LogFactory.getLog(StrategyRESTController.class);

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/strategy", method = RequestMethod.GET)
    public @ResponseBody AbstractResponse getStrategy(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());
        // Retrieve the strategies
        List<Strategy> strategy = strategyRepository.findByUserAndActiveTrue(user);
        List<StrategyDTO> strategyDTO = new LinkedList<>();
        for (Strategy curr : strategy) {
            strategyDTO.add(new StrategyDTO(curr));
        }
        LOGGER.debug("Get strategy API called.");
        return new SuccessResponse<>(strategyDTO);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/strategy", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse updateStrategy(@RequestBody List<StrategyDTO> strategyInput,
                                                         Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());

        // Disable the previous active strategy
        List<Strategy> previousActive = strategyRepository.findByUserAndActiveTrue(user);
        for (Strategy curr : previousActive) {
            curr.setActive(false);
            strategyRepository.save(curr);
        }

        // Insert new strategy
        for (StrategyDTO curr : strategyInput) {
            AssetClass assetClass = assetClassRepository.findOne(curr.getAssetClassId());
            Strategy newStrategy = Strategy.builder()
                    .user(user)
                    .assetClass(assetClass)
                    .percentage(curr.getPercentage())
                    .active(true)
                    .startingDate(new Date(Calendar.getInstance().getTimeInMillis())).build();
            strategyRepository.save(newStrategy);
            LOGGER.debug("Inserted strategy " + newStrategy);
        }

        // Set user as not new
        if(user.isNewUser()){
            user.setNewUser(false);
            userRepository.save(user);
        }

        return new SuccessResponse<>(null);
    }
}

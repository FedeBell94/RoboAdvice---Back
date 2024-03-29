package it.uiip.digitalgarage.roboadvice.businesslogic.controller;


import it.uiip.digitalgarage.roboadvice.businesslogic.exception.BadRequestException;
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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Class used to create all the API rest used to manage the {@link Strategy}.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "securedApi")
public class StrategyRESTController {

    private static final Log LOGGER = LogFactory.getLog(StrategyRESTController.class);

    private final UserRepository userRepository;
    private final StrategyRepository strategyRepository;
    private final AssetClassRepository assetClassRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public StrategyRESTController(final ModelMapper modelMapper, final UserRepository userRepository,
                                  final StrategyRepository strategyRepository,
                                  final AssetClassRepository assetClassRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.strategyRepository = strategyRepository;
        this.assetClassRepository = assetClassRepository;
    }

    /**
     * Retrieve the last active strategy for the caller {@link User}.
     *
     * @param authentication Represents the authentication token of an authenticated request.
     *
     * @return The last active {@link StrategyDTO} for the user, null in case the user has not a strategy set.
     */
    @RequestMapping(value = "/strategy", method = RequestMethod.GET)
    public AbstractResponse getActiveStrategy(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());

        // Retrieve the strategy
        List<Strategy> strategy = strategyRepository.findByUserAndActiveTrue(user);
        List<StrategyDTO> strategyDTO = new LinkedList<>();
        for (Strategy curr : strategy) {
            strategyDTO.add(modelMapper.map(curr, StrategyDTO.class));
        }
        LOGGER.debug("User: " + user.getUsername() + " - Get strategy API called.");
        return new SuccessResponse<>(strategyDTO);
    }

    /**
     * Insert the strategy passed if the {@link User} is new, or update the strategy if the {@link User} is not new.
     *
     * @param strategyInput  The new strategy to insert into the database.
     * @param authentication Represents the authentication token of an authenticated request.
     *
     * @return An empty {@link SuccessResponse}.
     *
     * @throws BadRequestException In case the total percentage of the strategy is different from 100%
     */
    @RequestMapping(value = "/strategy", method = RequestMethod.POST)
    public AbstractResponse updateStrategy(Authentication authentication,
                                           @RequestBody List<StrategyDTO> strategyInput) throws BadRequestException {
        if (strategyInput == null) {
            throw new BadRequestException("Bad request - missing parameter strategy.");
        }

        // Checking if the total percentage of the strategy is 100%
        BigDecimal percentage = BigDecimal.ZERO;
        for (StrategyDTO currStrategy : strategyInput) {
            percentage = percentage.add(currStrategy.getPercentage());
        }
        if (percentage.compareTo(BigDecimal.valueOf(100)) != 0) {
            throw new BadRequestException("Bad request - The percentage of the strategy passed is wrong.");
        }

        User user = userRepository.findByUsername(authentication.getName());

        // Set user as not new if he was new
        if (user.getIsNewUser()) {
            user.setIsNewUser(false);
            userRepository.save(user);
        } else {
            // Disable the previous active strategy or delete it if it is in the same day of the new one
            List<Strategy> previousActive = strategyRepository.findByUserAndActiveTrue(user);
            if (!previousActive.isEmpty() &&
                    previousActive.get(0).getStartingDate().compareTo(Date.valueOf(LocalDate.now())) == 0) {
                // Delete the strategy
                for (Strategy curr : previousActive) {
                    strategyRepository.delete(curr);
                }
            } else {
                // Disable the strategy
                for (Strategy curr : previousActive) {
                    curr.setActive(false);
                    strategyRepository.save(curr);
                }
            }
        }

        // Insert the new strategy
        List<Strategy> insertStrategy = new ArrayList<>();
        for (StrategyDTO curr : strategyInput) {
            AssetClass assetClass = assetClassRepository.findOne(curr.getAssetClassId());
            Strategy newStrategy = Strategy.builder()
                    .user(user)
                    .assetClass(assetClass)
                    .percentage(curr.getPercentage())
                    .active(true)
                    .startingDate(new Date(Calendar.getInstance().getTimeInMillis())).build();
            insertStrategy.add(newStrategy);
            LOGGER.debug("User: " + user.getUsername() + " - Inserted strategy " + newStrategy);
        }
        strategyRepository.save(insertStrategy);
        return new SuccessResponse<>(null);
    }
}

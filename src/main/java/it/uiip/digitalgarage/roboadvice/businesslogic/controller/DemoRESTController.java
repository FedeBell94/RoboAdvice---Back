package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.IDailyTaskUpdate;
import it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.dateProvider.LiarDateProvider;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.persistence.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to compute some portfolios just to see some computed portfolios. Used only to debug backend algorithms and
 * to debug frontend graphs and logic.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "securedApi")
public class DemoRESTController {

    @Autowired
    private IDailyTaskUpdate dailyTaskUpdate;

    @Autowired
    private UserRepository userRepository;

    private static final Log LOGGER = LogFactory.getLog(DemoRESTController.class);

    @RequestMapping(value = "/computePortfolioDemo", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse registerUser(@RequestBody JSONObject inputObject) {
        LOGGER.debug("Compute portfolio demo started.");
        Long startTime = System.currentTimeMillis();

        User user = userRepository.findOne((Integer) inputObject.get("user_id"));
        List<User> userList = new ArrayList<>();
        userList.add(user);
        LiarDateProvider liarDateProvider = new LiarDateProvider((String) inputObject.get("from"));
        for (int i = 0; i < (Integer) inputObject.get("days"); i++) {
            dailyTaskUpdate.executeUpdateTask(liarDateProvider, userList);
            liarDateProvider.goNextDay();
        }

        Long endTime = System.currentTimeMillis();
        LOGGER.debug("Compute portfolio demo ended -> execution time " + (endTime - startTime) + "ms. ");

        return new SuccessResponse<>(null);
    }
}

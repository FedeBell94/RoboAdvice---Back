package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.INightlyTask;
import it.uiip.digitalgarage.roboadvice.persistence.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Class used to compute some portfolios just to see some computed portfolios. Used only to debug backend algorithms and
 * to debug frontend graphs and logic.
 */
@RestController
@CrossOrigin(origins = "*")
public class DemoRESTController {

    private static final Log LOGGER = LogFactory.getLog(DemoRESTController.class);

    private final UserRepository userRepository;
    private final INightlyTask nightlyTask;

    @Autowired
    public DemoRESTController(final UserRepository userRepository, final INightlyTask nightlyTask){
        this.userRepository = userRepository;
        this.nightlyTask = nightlyTask;
    }

    @RequestMapping(value = "/computePortfolioDemo", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse registerUser(@RequestBody JSONObject inputObject) {
//        LOGGER.debug("Compute portfolio demo started.");
//        Long startTime = System.currentTimeMillis();
//
//        User user = userRepository.findOne(Long.parseLong((String)inputObject.get("user_id")));
//        java.sql.Date date = java.sql.Date.valueOf((String) inputObject.get("from"));
//        user.setLastPortfolioComputation(null);
//        user.setRegistration(date);
//        userRepository.save(user);
//        List<User> userList = new ArrayList<>();
//        userList.add(user);
//        LiarDateProvider liarDateProvider = new LiarDateProvider((String) inputObject.get("from"));
//        for (int i = 0; i < (Integer) inputObject.get("days"); i++) {
//            nightlyTask.executeNightlyTask(userList);
//            liarDateProvider.goNextDay();
//        }
//
//        Long endTime = System.currentTimeMillis();
//        LOGGER.debug("Compute portfolio demo ended -> execution time " + (endTime - startTime) + "ms. ");
        return new SuccessResponse<>(null);
    }
}

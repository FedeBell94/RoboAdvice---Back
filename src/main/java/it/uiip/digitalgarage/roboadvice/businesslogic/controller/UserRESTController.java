package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.dateProvider.LiarDateProvider;
import it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.IDailyTaskUpdate;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.UserDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.ErrorResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.ExchangeError;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.persistence.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Class used to create all the API rest used to manage the {@link User}.
 */
@RestController
@RequestMapping(value = "securedApi")
public class UserRESTController {

    @Autowired
    private UserRepository userRepository;

    private static final Log LOGGER = LogFactory.getLog(UserRESTController.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/loginUser", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse loginUser(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());
        LOGGER.debug("User " + user.getUsername() + " just logged in.");
        return new SuccessResponse<>(new UserDTO(user));
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/logoutUser", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse logoutUser(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        LOGGER.debug("User " + authentication.getName() + " just logged out.");
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        SecurityContextHolder.getContext().setAuthentication(null);
        return new SuccessResponse<>(null);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/tellMeWhoAmI", method = RequestMethod.GET)
    public @ResponseBody AbstractResponse tellMeWhoAmI(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());
        LOGGER.debug("TellWhoAmI: " + user.getUsername());
        return new SuccessResponse<>(new UserDTO(user));
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/registerUser", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse registerUser(@RequestBody UserDTO inputUser) {
        if(userRepository.findByUsername(inputUser.getUsername()) != null) {
            LOGGER.debug("Email already used for this user");
            return new ErrorResponse(ExchangeError.EMAIL_ALREADY_USED);
        }

        final String hashPassword = passwordEncoder.encode(inputUser.getPassword());
        final User user = User.builder()
                .username(inputUser.getUsername())
                .password(hashPassword)
                .nickname(inputUser.getNickname())
                .registration(new Date(Calendar.getInstance().getTime().getTime()))
                .enabled(true)
                .autoBalancing(false)
                .newUser(true)
                .build();
        userRepository.save(user);

        LOGGER.debug("User " + inputUser.getUsername() + " registered successfully");
        return new SuccessResponse<>(new UserDTO(user));
    }

    /***************************************************************************
     *                                                                         *
     *                                 DEMO                                    *
     *                                                                         *
    ***************************************************************************/
    @Autowired
    private IDailyTaskUpdate dailyTaskUpdate;

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/computePortfolioDemo", method = RequestMethod.GET)
    public @ResponseBody AbstractResponse registerUser() {
        LOGGER.debug("Night task started.");
        Long startTime = System.currentTimeMillis();

        User user = userRepository.findOne(1);
        List<User> userList = new ArrayList<>();
        userList.add(user);
        LiarDateProvider liarDateProvider = new LiarDateProvider("2017-02-01");
        for(int i = 0; i<15; i++) {
            dailyTaskUpdate.executeUpdateTask(liarDateProvider, userList);
            liarDateProvider.goNextDay();
        }

        Long endTime = System.currentTimeMillis();
        LOGGER.debug("Night task ended -> execution time " + (endTime - startTime) + "ms. ");

        return new SuccessResponse(null);
    }
}

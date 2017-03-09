package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.UserDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.ErrorResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.ExchangeError;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.persistence.model.Strategy;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.persistence.repository.StrategyRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.UserRepository;
import it.uiip.digitalgarage.roboadvice.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
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

    @Autowired
    private StrategyRepository strategyRepository;


    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/registerUser", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse registerUser(@RequestBody UserDTO inputUser) {

        final String hashPassword = passwordEncoder.encode(inputUser.getPassword());

        final User user = User.builder()
                .username(inputUser.getUsername())
                .password(hashPassword)
                .nickname(inputUser.getNickname())
                .registration(new Date(Calendar.getInstance().getTime().getTime()))
                .enabled(true)
                .autoBalancing(false)
                .build();

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            Logger.debug(UserRESTController.class, "Mail already used - user not registered");
            return new ErrorResponse(ExchangeError.EMAIL_ALREADY_USED);
        }

        Logger.debug(UserRESTController.class, "User " + inputUser.getUsername() + " registered successfully");
        return new SuccessResponse<>(new UserDTO(user));
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/loginUser", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse loginUser(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());
        Logger.debug(UserRESTController.class, "User " + user.getUsername() + " just logged in.");
        return new SuccessResponse<>(new UserDTO(user));
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/logoutUser", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse logoutUser(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        Logger.debug(UserRESTController.class, "User " + authentication.getName() + " just logged out.");
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        SecurityContextHolder.getContext().setAuthentication(null);
        return new SuccessResponse<>(null);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/tellMeWhoAmI", method = RequestMethod.GET)
    public @ResponseBody AbstractResponse tellMeWhoAmI(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());
        Logger.debug(UserRESTController.class, "TellWhoAmI: " + user.getUsername());
        return new SuccessResponse<>(new UserDTO(user));
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/isUserNew", method = RequestMethod.GET)
    public @ResponseBody AbstractResponse isUserNew(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());
        List<Strategy> strategyList = strategyRepository.findByUser(user);
        if(strategyList.isEmpty()){
            return new SuccessResponse<>(true);
        }
        return new SuccessResponse<>(false);
    }
}

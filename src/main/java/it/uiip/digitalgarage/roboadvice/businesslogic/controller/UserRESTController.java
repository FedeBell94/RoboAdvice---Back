package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.ErrorResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.ExchangeError;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.persistence.repository.PortfolioRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.UserRepository;
import it.uiip.digitalgarage.roboadvice.utils.AuthProvider;
import it.uiip.digitalgarage.roboadvice.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.utils.PasswordAuthentication;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@RestController
@SuppressWarnings("unused")
public class UserRESTController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PortfolioRepository p;

    private final PasswordAuthentication passwordAuth = new PasswordAuthentication(16);

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/registerUser", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse registerUser(@RequestBody User inputUser) {

        final String hashPassword = passwordAuth.hash(inputUser.getPassword().toCharArray());

        final User user = User.builder().email(inputUser.getEmail()).password(hashPassword).registration(
                new Date(Calendar.getInstance().getTime().getTime())).build();

        try {
            userRepository.save(user);
        } catch(DataIntegrityViolationException e){
            Logger.debug(UserRESTController.class, "Mail already used - user not registered");
            return new ErrorResponse(ExchangeError.EMAIL_ALREADY_USED);
        }

        Logger.debug(UserRESTController.class, "User " + inputUser.getEmail() + " registered successfully");
        return new SuccessResponse<>(user);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/loginUser", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse loginUser(@RequestBody User inputUser,
                                                    HttpServletResponse response) {

        User user = userRepository.findByEmail(inputUser.getEmail());

        if(user != null) {
            if(passwordAuth.authenticate(inputUser.getPassword().toCharArray(), user.getPassword())){
                // Set the user just registered in the authentication provider
                String userToken = AuthProvider.getInstance().setUserToken(user.getId());
                //response.addCookie(new Cookie("userToken", userToken));
                //response.addHeader("User-Token", userToken);
                Logger.debug(UserRESTController.class, "User " + user.getEmail() + " just logged in.");
                Map<String, Object> m = new HashMap<>();
                m.put("userToken", userToken);
                m.put("user", user);
                return new SuccessResponse<>(m);
            }
            Logger.debug(UserRESTController.class, "User " + user.getEmail() + " tried to log in with wrong password.");
            return new ErrorResponse(ExchangeError.WRONG_PASSWORD);
        }
        Logger.debug(UserRESTController.class, "Login: mail " + inputUser.getEmail() + " not found.");
        return new ErrorResponse(ExchangeError.WRONG_EMAIL);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/updateUserUsername", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse updateUserUsername(@RequestBody User inputUser, HttpServletRequest request) {
        String userToken = request.getHeader("User-Token");
        Integer userId = AuthProvider.getInstance().checkToken(userToken);
        if(userToken == null || userId == null){
            Logger.debug(StrategyRESTController.class, "Request with wrong user token.");
            return new ErrorResponse(ExchangeError.SECURITY_ERROR);
        }

        userRepository.setUserUsername(inputUser.getUsername(), userId);
        return new SuccessResponse<>(null);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/tellMeWhoAmI", method = RequestMethod.GET)
    public @ResponseBody AbstractResponse tellMeWhoAmI(HttpServletRequest request) {
        String userToken = request.getHeader("User-Token");
        Integer userId = AuthProvider.getInstance().checkToken(userToken);
        if(userToken == null || userId == null){
            Logger.debug(StrategyRESTController.class, "Request with wrong user token.");
            return new ErrorResponse(ExchangeError.SECURITY_ERROR);
        }

        User user = userRepository.findOne(userId);
        if(user != null){
            return new SuccessResponse<>(user);
        }
        return new ErrorResponse(ExchangeError.SECURITY_ERROR);
    }
}

package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.UserDTO;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.ErrorResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.ExchangeError;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.response.SuccessResponse;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.utils.AuthProvider;
import it.uiip.digitalgarage.roboadvice.utils.Logger;
import it.uiip.digitalgarage.roboadvice.utils.PasswordAuthentication;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Class used to create all the API rest used to manage the {@link User}.
 */
@RestController
@SuppressWarnings("unused")
public class UserRESTController extends AbstractController {

    private final PasswordAuthentication passwordAuth = new PasswordAuthentication(16);
    private final AuthProvider authProvider = AuthProvider.getInstance();

    /**
     * This mehtod register a new {@link User} on the system.
     *
     * @param inputUser
     *         The {@link User} to store.
     *
     * @return A {@link SuccessResponse} if everything has gone right, an {@link ErrorResponse} containing the error
     * code if something has gone wrong.
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/registerUser", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse registerUser(@RequestBody UserDTO inputUser) {

        final String hashPassword = passwordAuth.hash(inputUser.getPassword().toCharArray());

        final User user = User.builder().email(inputUser.getEmail()).password(hashPassword)
                .registration(new Date(Calendar.getInstance().getTime().getTime())).build();

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            Logger.debug(UserRESTController.class, "Mail already used - user not registered");
            return new ErrorResponse(ExchangeError.EMAIL_ALREADY_USED);
        }

        Logger.debug(UserRESTController.class, "User " + inputUser.getEmail() + " registered successfully");
        return new SuccessResponse<>(new UserDTO(user));
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/loginUser", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse loginUser(@RequestBody UserDTO inputUser, HttpServletResponse response) {

        User user = userRepository.findByEmail(inputUser.getEmail());
        if (user != null) {
            if (passwordAuth.authenticate(inputUser.getPassword().toCharArray(), user.getPassword())) {
                // Set the user just registered in the authentication provider
                String userToken = authProvider.bindUserToken(user);
                Logger.debug(UserRESTController.class, "User " + user.getEmail() + " just logged in.");
                Map<String, Object> resposeData = new HashMap<>();
                resposeData.put("userToken", userToken);
                resposeData.put("user", new UserDTO(user));
                return new SuccessResponse<>(resposeData);
            }
            Logger.debug(UserRESTController.class, "User " + user.getEmail() + " tried to log in with wrong password.");
            return new ErrorResponse(ExchangeError.WRONG_PASSWORD);
        }
        Logger.debug(UserRESTController.class, "Login: mail " + inputUser.getEmail() + " not found.");
        return new ErrorResponse(ExchangeError.WRONG_EMAIL);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/logoutUser", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse logoutUser(HttpServletRequest request) {
        return super.executeSafeTask(request, (user) -> {
            authProvider.removeUserToken(user);
            Logger.debug(UserRESTController.class, "Log out of user: " + user.getEmail());
            return new SuccessResponse(null);
        });
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/updateUserUsername", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse updateUserUsername(@RequestBody UserDTO inputUser, HttpServletRequest request) {

        return super.executeSafeTask(request, (user) -> {
            userRepository.setUserUsername(inputUser.getUsername(), user.getId());
            Logger.debug(UserRESTController.class, "Updated user " + user.getEmail() + " username.");
            return new SuccessResponse<>(new UserDTO(user));
        });
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/tellMeWhoAmI", method = RequestMethod.GET)
    public @ResponseBody AbstractResponse tellMeWhoAmI(HttpServletRequest request) {

        return super.executeSafeTask(request, (user) -> {
            Logger.debug(UserRESTController.class, "TellWhoAmI: " + user.getEmail());
            return new SuccessResponse<>(new UserDTO(user));
        });
    }
}

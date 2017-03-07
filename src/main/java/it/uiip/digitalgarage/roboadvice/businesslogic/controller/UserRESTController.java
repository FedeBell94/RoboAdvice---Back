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
     * This method register a new {@link User} on the system.
     *
     * @param inputUser
     *         The {@link User} to store.
     *
     * @return A {@link SuccessResponse} containing the {@link UserDTO} just registered if everything has gone right, or
     * an {@link ErrorResponse} containing the error code if something has gone wrong. Possible errors are:
     * EMAIL_ALREADY_USED.
     *
     * @see ExchangeError
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

    /**
     * This method perform the login of an user in the system.
     *
     * @param inputUser
     *         The {@link UserDTO} to log-in.
     *
     * @return A {@link SuccessResponse} containing the user token and the {@link UserDTO} just logged if everything has
     * gone right, or an {@link ErrorResponse} containing the error code if something has gone wrong. Possible errors
     * are: WRONG_PASSWORD, WRONG_EMAIL.
     *
     * @see ExchangeError
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/loginUser", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse loginUser(@RequestBody UserDTO inputUser) {

        User user = userRepository.findByEmail(inputUser.getEmail());
        if (user != null) {
            if (passwordAuth.authenticate(inputUser.getPassword().toCharArray(), user.getPassword())) {
                // Set the user just registered in the authentication provider
                String userToken = authProvider.bindUserToken(user);
                Logger.debug(UserRESTController.class, "User " + user.getEmail() + " just logged in.");
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("userToken", userToken);
                responseData.put("user", new UserDTO(user));
                return new SuccessResponse<>(responseData);
            }
            Logger.debug(UserRESTController.class, "User " + user.getEmail() + " tried to log in with wrong password.");
            return new ErrorResponse(ExchangeError.WRONG_PASSWORD);
        }
        Logger.debug(UserRESTController.class, "Login: mail " + inputUser.getEmail() + " not found.");
        return new ErrorResponse(ExchangeError.WRONG_EMAIL);
    }

    /**
     * This method perform the log-out of the user in the system.
     *
     * @param request
     *         The {@link HttpServletRequest} associated to the servlet.
     *
     * @return An empty {@link SuccessResponse} if everything has gone right, or an {@link ErrorResponse} containing the
     * error code if something has gone wrong. Possible errors are: SECURITY_ERROR.
     *
     * @see ExchangeError
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/logoutUser", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse logoutUser(HttpServletRequest request) {
        return super.executeSafeTask(request, (user) -> {
            authProvider.removeUserToken(user);
            Logger.debug(UserRESTController.class, "Log out of user: " + user.getEmail());
            return new SuccessResponse<>(null);
        });
    }

    /**
     * This method update/change the username of an {@link User}.
     *
     * @param inputUser
     *         The {@link UserDTO} to use to change the {@link User} username.
     * @param request
     *         The {@link HttpServletRequest} associated to the servlet.
     *
     * @return A {@link SuccessResponse} containing the {@link UserDTO} just updated if everything has gone right, or an
     * {@link ErrorResponse} containing the error code if something has gone wrong. Possible errors are:
     * SECURITY_ERROR.
     *
     * @see ExchangeError
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/updateUserUsername", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse updateUserUsername(@RequestBody UserDTO inputUser, HttpServletRequest request) {

        return super.executeSafeTask(request, (user) -> {
            user.setUsername(inputUser.getUsername());
            userRepository.setUserUsername(inputUser.getUsername(), user.getId());
            Logger.debug(UserRESTController.class, "Updated user " + user.getEmail() + " username.");
            return new SuccessResponse<>(new UserDTO(user));
        });
    }

    /**
     * This method returns the current {@link UserDTO} associated with the User-Token in the request.
     *
     * @param request
     *         The {@link HttpServletRequest} associated to the servlet.
     *
     * @return A {@link SuccessResponse} containing the {@link UserDTO} wanted if everything has gone right, or an
     * {@link ErrorResponse} containing the error code if something has gone wrong. Possible errors are:
     * SECURITY_ERROR.
     *
     * @see ExchangeError
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/tellMeWhoAmI", method = RequestMethod.GET)
    public @ResponseBody AbstractResponse tellMeWhoAmI(HttpServletRequest request) {

        return super.executeSafeTask(request, (user) -> {
            Logger.debug(UserRESTController.class, "TellWhoAmI: " + user.getEmail());
            return new SuccessResponse<>(new UserDTO(user));
        });
    }
}

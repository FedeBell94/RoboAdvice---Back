package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.dateProvider.DateProvider;
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

/**
 * Class used to create all the API rest used to manage the {@link User}.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "securedApi")
public class UserRESTController {

    @Autowired
    private UserRepository userRepository;

    private static final Log LOGGER = LogFactory.getLog(UserRESTController.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Perform the login of the user into the platform.
     *
     * @param authentication
     *         Represents the token for an authentication request or for an authenticated {@link User}.
     *
     * @return A {@link SuccessResponse} containing the {@link UserDTO} who asked for the login.
     */
    @RequestMapping(value = "/loginUser", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse loginUser(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());
        LOGGER.debug("User " + user.getUsername() + " just logged in.");
        return new SuccessResponse<>(new UserDTO(user));
    }

    /**
     * Performs the logout of the caller user fom the platform.
     *
     * @param authentication
     *         Represents the token for an authentication request or for an authenticated {@link User}.
     * @param request
     *         The {@link HttpServletRequest} of the servlet.
     * @param response
     *         The {@link HttpServletResponse} of the servlet.
     *
     * @return An empty {@link SuccessResponse}.
     */
    @RequestMapping(value = "/logoutUser", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse logoutUser(Authentication authentication, HttpServletRequest request,
                                                     HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        SecurityContextHolder.getContext().setAuthentication(null);
        LOGGER.debug("User " + authentication.getName() + " just logged out.");
        return new SuccessResponse<>(null);
    }

    /**
     * Returns to the caller his identity (as {@link User}).
     *
     * @param authentication
     *         Represents the token for an authentication request or for an authenticated {@link User}.
     *
     * @return Returns the {@link User} which has called this method.
     */
    @RequestMapping(value = "/tellMeWhoAmI", method = RequestMethod.GET)
    public @ResponseBody AbstractResponse tellMeWhoAmI(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());
        LOGGER.debug("TellWhoAmI: " + user.getUsername());
        return new SuccessResponse<>(new UserDTO(user));
    }

    /**
     * Register the {@link User} in the platform. This API is not secured.
     *
     * @param inputUser
     *         The {@link UserDTO} user to register.
     *
     * @return A {@link SuccessResponse} containing the user created, an {@link ErrorResponse} containing the error code
     * if something goes wrong during the registration.
     */
    @RequestMapping(value = "/registerUser", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse registerUser(@RequestBody UserDTO inputUser) {
        if (userRepository.findByUsername(inputUser.getUsername()) != null) {
            LOGGER.debug("Email already used for this user");
            return new ErrorResponse(ExchangeError.EMAIL_ALREADY_USED);
        }

        final String hashPassword = passwordEncoder.encode(inputUser.getPassword());
        final User user = User.builder()
                .username(inputUser.getUsername())
                .password(hashPassword)
                .nickname(inputUser.getNickname())
                .registration(new DateProvider().getToday())
                .enabled(true)
                .newUser(true)
                .build();
        userRepository.save(user);

        LOGGER.debug("User " + inputUser.getUsername() + " registered successfully");
        return new SuccessResponse<>(new UserDTO(user));
    }

}

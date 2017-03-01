package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.AbstractResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.ErrorResponse;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.ExchangeError;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.SuccessResponse;
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

@RestController
@SuppressWarnings("unused")
public class UserRESTController {

    @Autowired
    private UserRepository userRepository;

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
                response.addCookie(new Cookie("userToken", userToken));
                return new SuccessResponse<>(user);
            }
            return new ErrorResponse(ExchangeError.WRONG_PASSWORD);
        }
        return new ErrorResponse(ExchangeError.WRONG_EMAIL);

    }


    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/updateUserUsername", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse updateUserUsername(@CookieValue("userToken") String userToken) {
        if(userToken == null || !AuthProvider.getInstance().checkToken(userToken)){
            return new ErrorResponse(ExchangeError.SECURITY_ERROR);
        }
        return new SuccessResponse<>("Andato tutto bene");
    }

    /**
     * This REST api can log in a user after the proper credential check.
     * <p>
     * The call must be a POST and use the Spring Json parser to parse the
     * input.
     *
     * @param u
     *            the User who is trying to get logged in.
     * @param request
     *            the Http request sent with the POST.
     * @return the answer wrapper containing the success or the failure of the
     *         operation.
     * @see User
     *
     */
//	@RequestMapping(value = "/login", method = RequestMethod.POST)
//	public @ResponseBody Boolean logInUtente(@RequestBody User u, HttpServletRequest request) {
//
//		User user = new User();
//		if (u.getUsername().equals("noValue") || u.getPassword().equals("noValue")) {
//			return false;
//		} else {
//			// DBACCESS for user
//
//			PasswordAuthentication pa = new PasswordAuthentication(16);
//			char[] pass = u.getPassword().toCharArray();
//			if (!pa.authenticate(pass, user.getPassword())) {
//				return false;
//			}
//		}
//		user.setPassword("");
//		return true;
//	}

    /**
     * This REST api can register a new user storing the info he provided in the
     * form.
     * <p>
     * The call must be a POST and use the Spring Json parser to parse the
     * input.
     *
     * @param u
     *            the User bean carrying the user values provided in the form.
     * @param request
     *            the Http request sent with the POST.
     * @return the answer wrapper containing the success or the failure of the
     *         operation.
     * @see User
     *
     */
//	@RequestMapping(value = "/signUp", method = RequestMethod.POST)
//	public @ResponseBody Boolean signUp(@RequestBody User u, HttpServletRequest request) {
//
//		PasswordAuthentication pa = new PasswordAuthentication();
//		u.setPassword(pa.hash(u.getPassword().toCharArray()));
//
//		User user = new User();
//
//		// DBACCESS FOR REGISTRATION
//
//		user.setPassword("");
//		return true;
//	}
}

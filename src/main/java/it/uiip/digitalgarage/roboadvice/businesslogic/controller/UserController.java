package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import javax.servlet.http.HttpServletRequest;

import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.repository.UserRepository;
import it.uiip.digitalgarage.roboadvice.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import it.uiip.digitalgarage.roboadvice.persistence.model.AssetClass;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetClassRepository;
import it.uiip.digitalgarage.roboadvice.utils.PasswordAuthentication;

@RestController
@SuppressWarnings("unused")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	private final PasswordAuthentication passwordAuth = new PasswordAuthentication(8);

	@RequestMapping(value = "/registerUser", method = RequestMethod.POST)
	public @ResponseBody String registerUser(@RequestParam("email") String email,
											 @RequestParam("password") String password, HttpServletRequest request) {
		final String hashPassword = passwordAuth.hash(password.toCharArray());
		Logger.debug(UserController.class,
				"Register User method called: email -> " + email + ", password -> " + hashPassword);

		User user = User.builder().email(email).password(hashPassword).build();
		userRepository.save(user);
		return "done";
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
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public @ResponseBody Boolean logInUtente(@RequestBody User u, HttpServletRequest request) {

		User user = new User();
		if (u.getUsername().equals("noValue") || u.getPassword().equals("noValue")) {
			return false;
		} else {
			// DBACCESS for user

			PasswordAuthentication pa = new PasswordAuthentication(16);
			char[] pass = u.getPassword().toCharArray();
			if (!pa.authenticate(pass, user.getPassword())) {
				return false;
			}
		}
		user.setPassword("");
		return true;
	}

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

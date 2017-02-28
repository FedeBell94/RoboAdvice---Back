package it.uiip.digitalgarage.roboadvice.businesslogic.controller;

import java.util.concurrent.atomic.AtomicLong;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.*;

import it.uiip.digitalgarage.roboadvice.persistence.model.User;

@RestController
public class RESTApi {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @CrossOrigin(origins="*")
    @RequestMapping(value="/greeting", method= RequestMethod.POST)
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }
    
    /**
     * This REST api can log in a user after the proper credential check.
     * <p>
     * The call must be a POST and use the Spring Json parser to parse the
     * input.
     *
     * @param u the User who is trying to get logged in.
     * @param request the Http request sent with the POST.
     * @return the answer wrapper containing the success or the failure of the
     * operation.
     * @see User
     * 
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public @ResponseBody
    Boolean logInUtente(@RequestBody User u, HttpServletRequest request) {

        User user = new User();

        if (u.getUsername().equals("noValue") || u.getPassword().equals("noValue")) {
            
            return false;
        } else {
            //DBACCESS for user

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
     * @param u the User bean carrying the user values provided in the form.
     * @param request the Http request sent with the POST.
     * @return the answer wrapper containing the success or the failure of the
     * operation.
     * @see User
     * 
     */
    @RequestMapping(value = "/signUp", method = RequestMethod.POST)
    public @ResponseBody
    Boolean signUp(@RequestBody User u, HttpServletRequest request) {

        PasswordAuthentication pa = new PasswordAuthentication();
        u.setPassword(pa.hash(u.getPassword().toCharArray()));

        User user = new User();

        //DBACCESS FOR REGISTRATION

        user.setPassword("");
        return true;
    }
}

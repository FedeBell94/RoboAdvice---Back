package it.uiip.digitalgarage.roboadvice.businesslogic.security;

import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @CrossOrigin("*")
    @RequestMapping(value = "/api/test-credentials", method = RequestMethod.GET)
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }

    @CrossOrigin("*")
    @RequestMapping(value = "/api/login", method = RequestMethod.GET)
    public Greeting login(@RequestParam(value="name", defaultValue="World") String name) {

        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    @CrossOrigin("*")
    @RequestMapping(value = "/prova", method = RequestMethod.GET)
    public Greeting prova(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }
}

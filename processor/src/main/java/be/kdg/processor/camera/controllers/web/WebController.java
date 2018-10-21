package be.kdg.processor.camera.controllers.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * A web controller that handles general requests.
 *
 * @author Cedric Goffin
 */
@Controller
public class WebController {
    /**
     * Listens to requests made on the root (/) or /home url
     *
     * @return a string with the name of the html page
     */
    @RequestMapping(value = {"/", "home"})
    public String home() {
        return "home";
    }

    /**
     * Listens to requests made on the /admin url
     *
     * @return a string with the name of the html page
     */
    @RequestMapping(value = "/admin")
    public String admin() {
        return "admin";
    }

    /**
     * Listens to requests made on the /login url
     *
     * @return a string with the name of the html page
     */
    @RequestMapping(value = {"/login"})
    public String login() {
        return "login";
    }
}
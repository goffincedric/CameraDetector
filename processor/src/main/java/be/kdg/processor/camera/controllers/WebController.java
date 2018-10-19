package be.kdg.processor.camera.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Controller
public class WebController {
    @RequestMapping(value = {"/", "home"})
    public String home() {
        return "home";
    }

    @RequestMapping(value = "/admin")
    public String admin() {
        return "admin";
    }

    @RequestMapping(value = {"/login"})
    public String login() {
        return "login";
    }
}
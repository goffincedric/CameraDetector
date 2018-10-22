package be.kdg.processor.processor.controllers.web;

import be.kdg.processor.processor.Processor;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * A web controller that handles general requests.
 *
 * @author Cédric Goffin
 */
@Controller
public class ProcessorWebController {
    private final RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;
    private final Processor processor;

    @Autowired
    public ProcessorWebController(RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry, Processor processor) {
        this.rabbitListenerEndpointRegistry = rabbitListenerEndpointRegistry;
        this.processor = processor;
    }

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
     * @return a ModelAndView with the current status of the processor
     */
    @GetMapping(value = "/admin")
    public ModelAndView admin() {
        boolean isRunning = rabbitListenerEndpointRegistry.getListenerContainer("messageQueue").isRunning() && processor.isRunning();

        return new ModelAndView("admin", "processor.isRunning", isRunning);
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

    //TODO: Document
    @PostMapping(value = {"/toggleProcessor"})
    public ModelAndView toggleProcessor() {
        boolean isRunning = rabbitListenerEndpointRegistry.getListenerContainer("messageQueue").isRunning() && processor.isRunning();
        if (isRunning) {
            rabbitListenerEndpointRegistry.getListenerContainer("messageQueue").stop();
            processor.toggleProcessor();
            isRunning = false;
        } else {
            if (rabbitListenerEndpointRegistry.getListenerContainer("messageQueue").isRunning())
                rabbitListenerEndpointRegistry.getListenerContainer("messageQueue").start();
            if (processor.isRunning()) processor.toggleProcessor();
            isRunning = true;
        }
        return new ModelAndView("admin", "processor.isRunning", isRunning);
    }
}

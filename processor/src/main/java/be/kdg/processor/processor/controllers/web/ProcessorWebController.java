package be.kdg.processor.processor.controllers.web;

import be.kdg.processor.processor.Processor;
import be.kdg.processor.processor.dto.ProcessorSettingsDTO;
import be.kdg.processor.processor.services.SettingService;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    private final SettingService settingService;
    private final Processor processor;

    @Autowired
    public ProcessorWebController(RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry, SettingService settingService, Processor processor) {
        this.rabbitListenerEndpointRegistry = rabbitListenerEndpointRegistry;
        this.settingService = settingService;
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
        boolean isRunning = rabbitListenerEndpointRegistry.isRunning() && processor.isRunning();

        return new ModelAndView("admin", "processorstatus", isRunning);
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

    /**
     * Toggles the status of the processor to stop or start it
     *
     * @return a ModelAndView with the current status of the processor
     */
    @PostMapping(value = {"/toggleProcessor"})
    public ModelAndView toggleProcessor() {
        boolean isRunning = rabbitListenerEndpointRegistry.isRunning() && processor.isRunning();
        if (isRunning) {
            rabbitListenerEndpointRegistry.stop();
            processor.toggleProcessor();
            isRunning = false;
        } else {
            if (!rabbitListenerEndpointRegistry.isRunning()) rabbitListenerEndpointRegistry.start();
            if (!processor.isRunning()) processor.toggleProcessor();
            isRunning = true;
        }
        return new ModelAndView("redirect:/admin", "processorstatus", isRunning);
    }


    /**
     * Listens to GET requests made on the /processor/settings url
     *
     * @return the model containing the ProcessorSettingsDTO
     */
    @GetMapping("/processor/settings")
    public ModelAndView getFineFactors() {
        return new ModelAndView("processorsettings", "processorSettingsDTO", settingService.getProcessorSettingsDTO());
    }

    /**
     * Listens to POST requests made on the /processor/settings url
     *
     * @return the model containing the ProcessorSettingsDTO
     */
    @PostMapping("/processor/settings")
    public ModelAndView getFineFactors(@ModelAttribute ProcessorSettingsDTO processorSettingsDTO) {
        processorSettingsDTO = settingService.saveProcessorSettingsDTO(processorSettingsDTO);
        return new ModelAndView("processorsettings", "processorSettingsDTO", processorSettingsDTO);
    }
}

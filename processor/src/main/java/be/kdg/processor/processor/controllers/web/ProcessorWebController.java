package be.kdg.processor.processor.controllers.web;

import be.kdg.processor.processor.Processor;
import be.kdg.processor.processor.dom.ProcessorSettings;
import be.kdg.processor.processor.dto.ProcessorSettingsDTO;
import be.kdg.processor.processor.services.SettingService;
import org.modelmapper.ModelMapper;
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
 * @author C&eacute;dric Goffin
 */
@Controller
public class ProcessorWebController {
    private final RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;
    private final SettingService settingService;
    private final Processor processor;
    private final ModelMapper modelMapper;

    @Autowired
    public ProcessorWebController(RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry, SettingService settingService, Processor processor, ModelMapper modelMapper) {
        this.rabbitListenerEndpointRegistry = rabbitListenerEndpointRegistry;
        this.settingService = settingService;
        this.processor = processor;
        this.modelMapper = modelMapper;
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
     * Listens to requests made on the /login url
     *
     * @return a string with the name of the html page
     */
    @RequestMapping(value = {"/login"})
    public String login() {
        return "login";
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
    public ModelAndView getProcessorSettings() {
        ProcessorSettingsDTO processorSettingsDTO = modelMapper.map(settingService.getProcessorSettings(), ProcessorSettingsDTO.class);
        return new ModelAndView("processorsettings", "processorSettingsDTO", processorSettingsDTO);
    }

    /**
     * Listens to POST requests made on the /processor/settings url
     *
     * @param processorSettingsDTO a DTO containing the new Processor settings that need to be persisted to the database
     * @return the model containing the ProcessorSettingsDTO
     */
    @PostMapping("/processor/settings")
    public ModelAndView postProcessorSettings(@ModelAttribute ProcessorSettingsDTO processorSettingsDTO) {
        ProcessorSettings processorSettings = modelMapper.map(processorSettingsDTO, ProcessorSettings.class);
        processorSettings = settingService.saveProcessorSettings(processorSettings);
        processorSettingsDTO = modelMapper.map(processorSettings, ProcessorSettingsDTO.class);
        return new ModelAndView("redirect:/processor/settings?saved", "processorSettingsDTO", processorSettingsDTO);
    }
}

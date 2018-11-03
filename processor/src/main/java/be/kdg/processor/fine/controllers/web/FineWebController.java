package be.kdg.processor.fine.controllers.web;

import be.kdg.processor.processor.dom.FineSettings;
import be.kdg.processor.fine.dto.fineSettings.FineSettingsDTO;
import be.kdg.processor.processor.services.SettingService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * A web controller for Fine package. Mapped to listen to requests on /fine.
 *
 * @author C&eacute;dric Goffin
 */
@Controller
@RequestMapping("/fine")
public class FineWebController {
    private final SettingService settingService;
    private final ModelMapper modelMapper;

    /**
     * FineWebController constructor. Autowired via Spring.
     *
     * @param settingService is the service for the processor package. Can be used to access current processor settings
     * @param modelMapper is an autowired ModelMapper object
     */
    @Autowired
    public FineWebController(SettingService settingService, ModelMapper modelMapper) {
        this.settingService = settingService;
        this.modelMapper = modelMapper;
    }

    /**
     * Listens to GET requests made on the /fine/settings url
     *
     * @return the model containing the FineSettingsDTO
     */
    @GetMapping("/settings")
    public ModelAndView getFineFactors() {
        FineSettingsDTO fineSettingsDTO = modelMapper.map(settingService.getFineSettings(), FineSettingsDTO.class);
        return new ModelAndView("finesettings", "fineSettingsDTO", fineSettingsDTO);
    }

    /**
     * Listens to POST requests made on the /fine/settings url
     *
     * @param fineSettingsDTO a DTO containing the new Fine settings that need to be persisted to the database
     * @return the model containing the FineSettingsDTO
     */
    @PostMapping("/settings")
    public ModelAndView postFineFactors(@ModelAttribute FineSettingsDTO fineSettingsDTO) {
        FineSettings fineSettings = modelMapper.map(fineSettingsDTO, FineSettings.class);
        fineSettings = settingService.saveFineSettings(fineSettings);
        fineSettingsDTO = modelMapper.map(fineSettings, FineSettingsDTO.class);
        return new ModelAndView("redirect:/fine/settings?saved", "fineSettingsDTO", fineSettingsDTO);
    }
}

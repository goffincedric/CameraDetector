package be.kdg.processor.fine.controllers.web;

import be.kdg.processor.fine.dto.fineSettings.FineSettingsDTO;
import be.kdg.processor.processor.services.SettingService;
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

    /**
     * FineWebController constructor. Autowired via Spring.
     *
     * @param settingService is the service for the processor package. Can be used to access current processor settings
     */
    @Autowired
    public FineWebController(SettingService settingService) {
        this.settingService = settingService;
    }

    /**
     * Listens to GET requests made on the /fine/settings url
     *
     * @return the model containing the FineSettingsDTO
     */
    @GetMapping("/settings")
    public ModelAndView getFineFactors() {
        return new ModelAndView("finesettings", "fineSettingsDTO", settingService.getFineSettingsDTO());
    }

    /**
     * Listens to POST requests made on the /fine/settings url
     *
     * @param fineSettingsDTO a DTO containing the new Fine settings that need to be persisted to the database
     * @return the model containing the FineSettingsDTO
     */
    @PostMapping("/settings")
    public ModelAndView getFineFactors(@ModelAttribute FineSettingsDTO fineSettingsDTO) {
        fineSettingsDTO = settingService.saveFineSettingsDTO(fineSettingsDTO);
        return new ModelAndView("finesettings", "fineSettingsDTO", fineSettingsDTO);
    }
}

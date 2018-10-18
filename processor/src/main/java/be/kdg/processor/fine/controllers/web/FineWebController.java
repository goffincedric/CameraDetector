package be.kdg.processor.fine.controllers.web;

import be.kdg.processor.fine.dto.fineOptions.FineOptionsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Cédric Goffin
 * 18/10/2018 22:40
 */
@Controller
@RequestMapping("/fine")
public class FineWebController {
    private final FineOptionsDTO fineOptionsDTO;

    @Autowired
    public FineWebController(FineOptionsDTO fineOptionsDTO) {
        this.fineOptionsDTO = fineOptionsDTO;
    }

    @GetMapping("/settings")
    public ModelAndView getFineFactors() {
        return new ModelAndView("finesettings", "fineOptionsDTO", fineOptionsDTO);
    }
}

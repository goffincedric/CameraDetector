package be.kdg.processor.fine.controllers.web;

import be.kdg.processor.fine.dto.fineOptions.FineOptionsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * A web controller for Fine package. Mapped to listen to requests on /fine.
 *
 * @author Cédric Goffin
 */
@Controller
@RequestMapping("/fine")
public class FineWebController {
    private final FineOptionsDTO fineOptionsDTO;

    /**
     * FineWebController constructor. Autowired via Spring.
     *
     * @param fineOptionsDTO is a DTO containing the current factors used in the fine amount calculation
     */
    @Autowired
    public FineWebController(FineOptionsDTO fineOptionsDTO) {
        this.fineOptionsDTO = fineOptionsDTO;
    }

    /**
     * Listens to GET requests made on the /settings url
     *
     * @return the model containing the FineOptionsDTO
     */
    @GetMapping("/settings")
    public ModelAndView getFineFactors() {
        return new ModelAndView("finesettings", "fineOptionsDTO", fineOptionsDTO);
    }
}

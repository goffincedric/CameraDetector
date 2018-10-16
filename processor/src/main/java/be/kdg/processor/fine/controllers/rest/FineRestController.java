package be.kdg.processor.fine.controllers.rest;

import be.kdg.processor.fine.dom.EmissionFine;
import be.kdg.processor.fine.dom.Fine;
import be.kdg.processor.fine.dom.SpeedingFine;
import be.kdg.processor.fine.dto.EmissionFineDTO;
import be.kdg.processor.fine.dto.FineDTO;
import be.kdg.processor.fine.dto.SpeedingFineDTO;
import be.kdg.processor.fine.exceptions.FineException;
import be.kdg.processor.fine.services.FineService;
import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Cédric Goffin
 * 16/10/2018 16:34
 */
@RestController
@RequestMapping("/api")
public class FineRestController {

    private final ModelMapper modelMapper;
    private final FineService fineService;

    public FineRestController(FineService fineService, ModelMapper modelMapper) {
        this.fineService = fineService;
        this.modelMapper = modelMapper;
    }

    //http://localhost:8080/api/fine/16-10-2018%2017:00:00/16-10-2018%2018:00:00
    @GetMapping("/fine/{from}/{to}")
    public ResponseEntity<List<FineDTO>> loadGreeting(@PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss") LocalDateTime from, @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss") LocalDateTime to) throws FineException {
        List<Fine> fines = fineService.getFinesBetween(from, to);
        return new ResponseEntity<>(fines.stream()
                .map(f -> {
                    if (f instanceof EmissionFine) return modelMapper.map(f, EmissionFineDTO.class);
                    else if (f instanceof SpeedingFine) return modelMapper.map(f, SpeedingFineDTO.class);
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()),
                HttpStatus.OK);
    }
}

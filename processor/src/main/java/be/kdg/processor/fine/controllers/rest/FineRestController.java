package be.kdg.processor.fine.controllers.rest;

import be.kdg.processor.fine.dom.EmissionFine;
import be.kdg.processor.fine.dom.Fine;
import be.kdg.processor.fine.dom.SpeedingFine;
import be.kdg.processor.fine.dto.changeFineAmout.ChangeFineAmountDTO;
import be.kdg.processor.fine.dto.fine.EmissionFineDTO;
import be.kdg.processor.fine.dto.fine.FineDTO;
import be.kdg.processor.fine.dto.fine.SpeedingFineDTO;
import be.kdg.processor.fine.exceptions.FineException;
import be.kdg.processor.fine.services.FineService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Rest controller for Fine package. Mapped to listen to requests on /api/fine
 *
 * @author C&eacute;dric Goffin
 */
@RestController
@RequestMapping("/api/fine")
public class FineRestController {
    private final ModelMapper modelMapper;
    private final FineService fineService;

    /**
     * FineRestController constructor. Autowired via Spring.
     *
     * @param fineService is the service for the Fine package
     * @param modelMapper is a mapper that most methods use to map an object to DTO and vice versa
     */
    @Autowired
    public FineRestController(FineService fineService, ModelMapper modelMapper) {
        this.fineService = fineService;
        this.modelMapper = modelMapper;
    }

    /**
     * Method that handles incoming GET requests from /api/fine/{from}/{to}.
     *
     * @param from is the starting time for fines to be returned
     * @param to   is the ending to for fines to be returned
     * @return a list of FineDTOs which contain information about fines that where made between the 'from' and 'to' timestamps
     * @throws FineException when the given timestamps aren't in the right order
     */
    //http://localhost:8080/api/fine/get?from=16-10-2018_00:00:00&to=16-10-2018_00:00:00
    @GetMapping("/get")
    public ResponseEntity<List<FineDTO>> getFinesBetween(@RequestParam(value = "from") @DateTimeFormat(pattern = "dd-MM-yyyy_HH:mm:ss") LocalDateTime from, @RequestParam(value = "to") @DateTimeFormat(pattern = "dd-MM-yyyy_HH:mm:ss") LocalDateTime to) throws FineException {
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

    /**
     * Method that handles incoming PUT requests from /api/fine/acceptFine/{id}. Gets used to set a fine as accepted.
     *
     * @param id is the id of the fine to accept
     * @return a FineDTO object containing the information about the accepted Fine
     * @throws FineException when no Fine could be found with the supplied fine id.
     */
    @PutMapping("/acceptFine/{id}")
    public ResponseEntity<FineDTO> acceptFine(@PathVariable int id) throws FineException {
        Optional<Fine> optionalFine = fineService.acceptFine(id);
        if (optionalFine.isPresent()) {
            Fine fine = optionalFine.get();
            FineDTO fineDTO = null;
            if (fine instanceof EmissionFine) fineDTO = modelMapper.map(fine, EmissionFineDTO.class);
            else if (fine instanceof SpeedingFine) fineDTO = modelMapper.map(fine, SpeedingFineDTO.class);

            return new ResponseEntity<>(fineDTO, HttpStatus.OK);
        }
        throw new FineException("Fine with id " + id + "could not be updated!");
    }

    /**
     * Method that handles incoming PATCH requests from /api/fine/changeFineAmount/{id}. Gets used to change the fine amount and motivation.
     *
     * @param id    is the id of the Fine to patch
     * @param patch is a map containing the patched amount and motivation
     * @return a ChangeFineAmountDTO containing the information about the changed Fine
     * @throws FineException when no Fine could be found with the supplied fine id.
     */
    @PatchMapping("/changeFineAmount/{id}")
    public ResponseEntity<ChangeFineAmountDTO> changeFineAmount(@PathVariable int id, @RequestBody Map<String, Object> patch) throws FineException {
        patch = patch.entrySet().stream().filter(set -> set.getKey().equalsIgnoreCase("amount") || set.getKey().equalsIgnoreCase("motivation")).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Optional<Fine> optionalFine = fineService.getFine(id);
        if (optionalFine.isPresent()) {
            Fine fine = optionalFine.get();

            modelMapper.map(patch, fine);
            fineService.save(fine);

            return new ResponseEntity<>(modelMapper.map(fine, ChangeFineAmountDTO.class),
                    HttpStatus.OK);
        }
        throw new FineException("Fine with id " + id + "could not be updated!");
    }
}

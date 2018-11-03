package be.kdg.processor.fine.services;

import be.kdg.processor.fine.dom.Fine;
import be.kdg.processor.fine.exceptions.FineException;
import be.kdg.processor.fine.repository.FineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Service used to manipulate Fine information from the H2 in-memory database.
 *
 * @author C&eacute;dric Goffin
 * @see FineRepository
 * @see FineCalculationService
 */
@Service
@Transactional
public class FineService {
    private static final Logger LOGGER = Logger.getLogger(FineService.class.getName());


    private final FineRepository fineRepository;
    private final FineDetectionService fineDetectionService;

    /**
     * Constructor used by Spring framework to initialize the service as a bean
     *
     * @param fineRepository       the repository that has access to the H2 in-memory database
     * @param fineDetectionService a helper service that detects future fines from cameramessages
     */
    @Autowired
    public FineService(FineRepository fineRepository, FineDetectionService fineDetectionService) {
        this.fineRepository = fineRepository;
        this.fineDetectionService = fineDetectionService;
    }

    /**
     * Gets information about a Fine.
     *
     * @param id is the fine id
     * @return an Optional Fine. Is empty when no Fine could be found or when an error occurred.
     */
    public Optional<Fine> getFine(int id) {
        return fineRepository.findById(id);
    }

    /**
     * Gets all Fines from the repository that are linked to the supplied Licenseplate id.
     *
     * @param licenseplateId is the identifier of a Licenseplate
     * @return a list of Fines that are linked to the supplied Licenseplate id
     */
    public List<Fine> getFinesByLicenseplate(String licenseplateId) {
        return fineRepository.findAllByLicenseplateId(licenseplateId);
    }

    /**
     * Gets all Fines from the repository that where made between the 'from' and 'to' timestamps.
     *
     * @param from is the starting time for fines to be returned
     * @param to   is the ending to for fines to be returned
     * @return a list of Fines that where made between the 'from' and 'to' timestamps
     * @throws FineException when the given timestamps aren't in the right order
     */
    public List<Fine> getFinesBetween(String from, String to) throws FineException {
        LocalDateTime fromDate = LocalDateTime.parse(from);
        LocalDateTime toDate = LocalDateTime.parse(to);
        if (fromDate.isAfter(toDate)) throw new FineException("From date (" + fromDate + ") is after to date (" + toDate + ")");
        return fineRepository.findAllByTimestampBetween(fromDate, toDate);
    }

    /**
     * Updates a Fine from the repository to set its status as accepted.
     *
     * @param id is the id of the fine to set as accepted
     * @return an Optional accepted Fine. Can be empty when no Fine was found for the supplied id.
     */
    public Optional<Fine> acceptFine(int id) {
        Optional<Fine> optionalFine = getFine(id);
        if (optionalFine.isPresent()) {
            Fine fine = optionalFine.get();
            fine.setAccepted(true);
            optionalFine = Optional.of(save(fine));
            return optionalFine;
        }
        return Optional.empty();
    }

    /**
     * Creates a Fine in the repository
     *
     * @param fine is the Fine to persist to the database
     * @return the persisted Fine from the repository
     */
    public Fine save(Fine fine) {
        return fineRepository.save(fine);
    }

    /**
     * Creates multiple Fines in the repository
     *
     * @param fines is a list of Fines that will be persisted to the database.
     * @return a list of Fines from the repository that were persisted to the database
     */
    public List<Fine> saveFines(List<Fine> fines) {
        fines = fineRepository.saveAll(fines);
        return fines;
    }
}

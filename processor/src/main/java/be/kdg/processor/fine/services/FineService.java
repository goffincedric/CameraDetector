package be.kdg.processor.fine.services;

import be.kdg.processor.camera.dom.Camera;
import be.kdg.processor.camera.dom.CameraMessage;
import be.kdg.processor.camera.dom.CameraType;
import be.kdg.processor.camera.services.CameraServiceAdapter;
import be.kdg.processor.fine.dom.Fine;
import be.kdg.processor.fine.exceptions.FineException;
import be.kdg.processor.fine.repository.FineRepository;
import be.kdg.processor.licenseplate.dom.Licenseplate;
import be.kdg.processor.licenseplate.exception.LicensePlateException;
import be.kdg.processor.licenseplate.services.LicenseplateServiceAdapter;
import be.kdg.processor.processor.dom.DoubleSetting;
import be.kdg.processor.processor.dom.IntSetting;
import be.kdg.processor.processor.services.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Service used to manipulate Fine information from the H2 in-memory database.
 *
 * @author C&eacute;dric Goffin
 * @see FineRepository
 * @see FineCalculator
 */
@Service
@Transactional
public class FineService {
    private static final Logger LOGGER = Logger.getLogger(FineService.class.getName());

    private final CameraServiceAdapter cameraServiceAdapter;
    private final LicenseplateServiceAdapter licenseplateServiceAdapter;
    private final SettingService settingService;

    private final FineRepository fineRepository;
    private final FineCalculator fineCalculator;

    @Value("${fine.emission.timeframe_days}")
    private int emissionTimeFrameDays;
    @Value("${fine.emission.fineFactor}")
    private double emissionFineFactor;
    @Value("${fine.speed.fineFactor.slow}")
    private double speedFineFactorSlow;
    @Value("${fine.speed.fineFactor.fast}")
    private double speedFineFactorFast;
    @Value("${fine.paymentDeadlineDays}")
    private int paymentDeadlineDays;

    /**
     * Constructor used by Spring framework to initialize the service as a bean
     *
     * @param cameraServiceAdapter       the service for the Camera package
     * @param licenseplateServiceAdapter the service for the Lincenseplate package
     * @param settingService             the service for the processor package. Manages all processor settings.
     * @param fineRepository             the repository that has access to the H2 in-memory database
     * @param fineCalculator             a helper class used to calculate fines
     */
    @Autowired
    public FineService(CameraServiceAdapter cameraServiceAdapter, LicenseplateServiceAdapter licenseplateServiceAdapter, SettingService settingService, FineRepository fineRepository, FineCalculator fineCalculator) {
        this.cameraServiceAdapter = cameraServiceAdapter;
        this.licenseplateServiceAdapter = licenseplateServiceAdapter;
        this.settingService = settingService;
        this.fineRepository = fineRepository;
        this.fineCalculator = fineCalculator;
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
     * Gets all Fines from the repository that where made between the 'from' and 'to' timestamps.
     *
     * @param from is the starting time for fines to be returned
     * @param to   is the ending to for fines to be returned
     * @return a list of Fines that where made between the 'from' and 'to' timestamps
     * @throws FineException when the given timestamps aren't in the right order
     */
    public List<Fine> getFinesBetween(LocalDateTime from, LocalDateTime to) throws FineException {
        if (from.isAfter(to)) throw new FineException("From date (" + from + ") is after to date (" + to + ")");
        return fineRepository.findAllByTimestampBetween(from, to);
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

    /**
     * Method that processes CameraMessages and persists any detected fines to the database
     *
     * @param messages is a list of CameraMessages that need to be checked for emission/speeding violations
     * @return all CameraMessages that could not be processed
     */
    public List<CameraMessage> processFines(List<CameraMessage> messages) {
        // Get current settings
        Optional<IntSetting> optionalEmissionTimeFrameDays = settingService.getIntSetting("emissionTimeFrameDays");
        optionalEmissionTimeFrameDays.ifPresent(s -> emissionTimeFrameDays = s.getIntValue());
        Optional<DoubleSetting> optionalEmissionFineFactor = settingService.getDoubleSetting("emissionFineFactor");
        optionalEmissionFineFactor.ifPresent(s -> emissionFineFactor = s.getDoubleValue());
        Optional<DoubleSetting> optionalSpeedFineFactorSlow = settingService.getDoubleSetting("speedFineFactorSlow");
        optionalSpeedFineFactorSlow.ifPresent(s -> speedFineFactorSlow = s.getDoubleValue());
        Optional<DoubleSetting> optionalSpeedFineFactorFast = settingService.getDoubleSetting("speedFineFactorFast");
        optionalSpeedFineFactorFast.ifPresent(s -> speedFineFactorFast = s.getDoubleValue());
        Optional<IntSetting> optionalPaymentDeadlineDays = settingService.getIntSetting("paymentDeadlineDays");
        optionalPaymentDeadlineDays.ifPresent(s -> paymentDeadlineDays = s.getIntValue());

        // Filter out speedmessages
        List<CameraMessage> speedMessages = cameraServiceAdapter.getMessagesFromTypes(messages, List.of(CameraType.SPEED, CameraType.SPEED_EMISSION));
        // Filter out emissionmessages
        List<CameraMessage> emissionMessages = cameraServiceAdapter.getMessagesFromTypes(messages, List.of(CameraType.EMISSION, CameraType.SPEED_EMISSION));

        // Init unprocessed messages list with messages that where not speed/emissionmessages
        List<CameraMessage> unprocessed = messages.stream().filter(m -> !speedMessages.contains(m) && !emissionMessages.contains(m)).collect(Collectors.toList());

        // Process emissionmessages
        Map.Entry<List<Fine>, List<CameraMessage>> emissionPair = processEmissionFines(emissionMessages);

        // Process speedmessages
        Map.Entry<List<Fine>, List<CameraMessage>> speedPair = processSpeedingFines(speedMessages);

        // List of unprocessed messages to return
        unprocessed.addAll(emissionPair.getValue());
        unprocessed.addAll(speedPair.getValue());

        // Merge emission and speed fine lists
        List<Fine> fines = new ArrayList<>() {{
            addAll(emissionPair.getKey());
            addAll(speedPair.getKey());
        }};

        // Link fines to licenseplates
        fines.forEach(f -> f.getLicenseplate().getFines().add(f));

        // Save all fines
        saveFines(fines);

        return unprocessed;
    }

    /**
     * Method that processes all CameraMessages that are linked to emission cameras and detects any emission-related violations.
     *
     * @param emissionMessages is a list of all CameraMessages that are linked to emission cameras
     * @return a Map Entry with a list of Fines as key and a list of unprocessed CameraMessages as value
     */
    public Map.Entry<List<Fine>, List<CameraMessage>> processEmissionFines(List<CameraMessage> emissionMessages) {
        // List of unprocessed messages to return
        List<CameraMessage> unprocessed = new ArrayList<>();

        // Process emissionMessages and get only 1 fine per licenseplate per day
        List<Fine> emissionFines = emissionMessages.stream()
                .map(m -> {
                    Optional<Licenseplate> optionalLicenseplate = Optional.empty();
                    Optional<Camera> optionalCamera = cameraServiceAdapter.getCamera(m.getCameraId());
                    if (optionalCamera.isPresent()) {
                        Camera camera = optionalCamera.get();

                        try {
                            if (m.getLicenseplate() != null) {
                                optionalLicenseplate = licenseplateServiceAdapter.getLicensePlate(m.getLicenseplate());
                            } else if (m.getCameraImage() != null) {
                                optionalLicenseplate = licenseplateServiceAdapter.getLicensePlate(m.getCameraImage());
                            }
                        } catch (LicensePlateException e) {
                            LOGGER.severe(e.getMessage());
                        }

                        if (optionalLicenseplate.isPresent()) {
                            Licenseplate licenseplate = optionalLicenseplate.get();
                            if (camera.getEuroNorm() > licenseplate.getEuroNumber()) {
                                if (licenseplate.getFines().stream().noneMatch(f -> f.getTimestamp().isAfter(m.getTimestamp().minusDays(emissionTimeFrameDays)))) {
                                    return fineCalculator.calcEmissionFine(camera, licenseplate, emissionFineFactor, paymentDeadlineDays);
                                }
                            }
                        }
                    }
                    unprocessed.add(m);
                    return Optional.empty();
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(f -> (Fine) f)
                .distinct()
                .collect(Collectors.toList());

        // Return fines and unprocessed messages
        return Map.entry(emissionFines, unprocessed);
    }

    /**
     * Method that processes all CameraMessages that are linked to speeding cameras and detects any speeding-related violations.
     *
     * @param speedMessages is a list of all CameraMessages that are linked to speed cameras
     * @return a Map Entry with a list of Fines as key and a list of unprocessed CameraMessages as value
     */
    public Map.Entry<List<Fine>, List<CameraMessage>> processSpeedingFines(List<CameraMessage> speedMessages) {
        // Link messages
        Map.Entry<Map<CameraMessage, CameraMessage>, List<CameraMessage>> speedPair = linkMessages(speedMessages);

        // List of unprocessed messages to return
        List<CameraMessage> unprocessed = new ArrayList<>(speedPair.getValue());

        // Detect speedfines
        List<Fine> speedFines = speedPair.getKey().entrySet().stream()
                .map(pair -> {
                    Optional<Camera> optionalCamera = cameraServiceAdapter.getCamera(pair.getKey().getCameraId());
                    if (optionalCamera.isPresent()) {
                        Camera camera = optionalCamera.get();

                        Optional<Licenseplate> optionalLicenseplate = Optional.empty();
                        try {
                            optionalLicenseplate = licenseplateServiceAdapter.getLicensePlate(pair.getKey().getLicenseplate());
                        } catch (LicensePlateException e) {
                            LOGGER.severe(e.getMessage());
                        }
                        if (optionalLicenseplate.isPresent()) {
                            Licenseplate licenseplate = optionalLicenseplate.get();
                            return fineCalculator.calcSpeedFine(pair, camera, licenseplate, speedFineFactorSlow, speedFineFactorFast, paymentDeadlineDays);
                        }
                    }
                    unprocessed.add(pair.getKey());
                    unprocessed.add(pair.getValue());
                    return Optional.empty();
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(f -> (Fine) f)
                .collect(Collectors.toList());

        // Return fines and unprocessed messages
        return Map.entry(speedFines, unprocessed);
    }

    /**
     * Method that links speeding-related CameraMessages to each other.
     * Two messages are seen as linked when one messages describes when a car with a license plate passed one camera and later passes the second camera linked to the first camera.
     *
     * @param messages is a list of speeding-related CameraMessages that need to be linked
     * @return a Map Entry with a Map of linked messages as its key and a list of unprocessed CameraMessages as its value
     */
    private Map.Entry<Map<CameraMessage, CameraMessage>, List<CameraMessage>> linkMessages(List<CameraMessage> messages) {
        Map<CameraMessage, CameraMessage> linkedMessages = new HashMap<>();
        messages.sort(Comparator.comparing(CameraMessage::getTimestamp));
        List<CameraMessage> unprocessedMessages = new ArrayList<>(messages);

        for (CameraMessage message : messages) {
            if (!linkedMessages.containsKey(message) || !linkedMessages.containsValue(message)) {
                Optional<Camera> optionalCamera = cameraServiceAdapter.getCamera(message.getCameraId());
                if (optionalCamera.isPresent()) {
                    Camera camera = optionalCamera.get();

                    if (camera.getSegment() != null) {
                        Optional<CameraMessage> optionalLinkedMessage = Optional.empty();
                        try {
                            optionalLinkedMessage = unprocessedMessages.stream()
                                    .filter(m ->
                                            m.getCameraId() == camera.getSegment().getConnectedCameraId() &&
                                                    m.getLicenseplate().equalsIgnoreCase(message.getLicenseplate()) &&
                                                    m.getTimestamp().isAfter(message.getTimestamp()))
                                    .findFirst();
                        } catch (Exception e) {
                            LOGGER.severe(String.format("Could not link message %s", message));
                        }

                        optionalLinkedMessage.ifPresent(m -> {
                            if (camera.getSegment() != null) {
                                linkedMessages.put(message, m);
                            } else {
                                linkedMessages.put(m, message);
                            }
                            unprocessedMessages.remove(message);
                            unprocessedMessages.remove(m);
                        });
                    }
                }
            }
        }

        return Map.entry(linkedMessages, unprocessedMessages);
    }
}

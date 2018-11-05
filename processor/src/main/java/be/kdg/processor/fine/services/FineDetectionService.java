package be.kdg.processor.fine.services;

import be.kdg.processor.camera.dom.Camera;
import be.kdg.processor.camera.dom.CameraMessage;
import be.kdg.processor.camera.dom.CameraType;
import be.kdg.processor.camera.services.CameraServiceAdapter;
import be.kdg.processor.fine.dom.Fine;
import be.kdg.processor.licenseplate.dom.Licenseplate;
import be.kdg.processor.licenseplate.services.LicenseplateServiceAdapter;
import be.kdg.processor.processor.dom.DoubleSetting;
import be.kdg.processor.processor.dom.IntSetting;
import be.kdg.processor.processor.services.SettingService;
import be.kdg.sa.services.CameraNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Helper class to detect which messages should get fined
 *
 * @author Cédric Goffin
 */
@Component
public class FineDetectionService {
    private static final Logger LOGGER = Logger.getLogger(FineService.class.getName());

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


    private final CameraServiceAdapter cameraServiceAdapter;
    private final LicenseplateServiceAdapter licenseplateServiceAdapter;
    private final FineCalculationService fineCalculationService;
    private final SettingService settingService;

    /**
     * Constructor used by Spring framework to initialize the service as a bean
     *
     * @param cameraServiceAdapter       the service for the Camera package
     * @param licenseplateServiceAdapter the service for the Lincenseplate package
     * @param fineCalculationService     a helper class used to calculate fines
     * @param settingService             the service for the processor package. Manages all processor settings.
     */
    @Autowired
    public FineDetectionService(CameraServiceAdapter cameraServiceAdapter, LicenseplateServiceAdapter licenseplateServiceAdapter, FineCalculationService fineCalculationService, SettingService settingService) {
        this.cameraServiceAdapter = cameraServiceAdapter;
        this.licenseplateServiceAdapter = licenseplateServiceAdapter;
        this.fineCalculationService = fineCalculationService;
        this.settingService = settingService;
    }

    /**
     * Gets and updates current processor settings from settingsrepository;
     */
    public void updateProcessorSettings() {
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
    }

    /**
     * Method that processes CameraMessages and persists any detected fines to the database
     *
     * @param messages is a list of CameraMessages that need to be checked for emission/speeding violations
     * @return List of fines and all CameraMessages that could not be processed
     */
    public Map.Entry<List<Fine>, List<CameraMessage>> processMessages(List<CameraMessage> messages) {
        // Update detection
        updateProcessorSettings();

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

        // Return fines + unprocessed
        return Map.entry(fines, unprocessed);
    }

    /**
     * Method that processes all CameraMessages that are linked to emission cameras and detects any emission-related violations.
     *
     * @param emissionMessages is a list of all CameraMessages that are linked to emission cameras
     * @return a Map Entry with a list of Fines as key and a list of unprocessed CameraMessages as value
     */
    Map.Entry<List<Fine>, List<CameraMessage>> processEmissionFines(List<CameraMessage> emissionMessages) {
        // List of unprocessed messages to return
        List<CameraMessage> unprocessed = new ArrayList<>();

        // Process emissionMessages and get only 1 fine per licenseplate per day
        List<Fine> emissionFines = emissionMessages.stream()
                .map(m -> {
                    try {
                        Optional<Camera> optionalCamera = cameraServiceAdapter.getCamera(m.getCameraId());
                        Optional<Licenseplate> optionalLicenseplate = Optional.empty();
                        if (optionalCamera.isPresent()) {
                            if (m.getLicenseplate() != null)
                                optionalLicenseplate = licenseplateServiceAdapter.getLicensePlate(m.getLicenseplate());
                            else if (m.getCameraImage() != null)
                                optionalLicenseplate = licenseplateServiceAdapter.getLicensePlate(m.getCameraImage());

                            if (optionalLicenseplate.isPresent()) {
                                if (optionalCamera.get().getEuroNorm() > optionalLicenseplate.get().getEuroNumber() &&
                                        optionalLicenseplate.get().getFines().stream().noneMatch(f -> f.getTimestamp().isAfter(m.getTimestamp().minusDays(emissionTimeFrameDays))))
                                    return fineCalculationService.calcEmissionFine(optionalCamera.get(), optionalLicenseplate.get(), emissionFineFactor, paymentDeadlineDays);
                                else return Optional.empty();
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.severe(e.getMessage());
                    }

                    unprocessed.add(m);
                    return Optional.empty();
                })
                .filter(Optional::isPresent)
                .map(optional -> (Fine) optional.get())
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
    Map.Entry<List<Fine>, List<CameraMessage>> processSpeedingFines(List<CameraMessage> speedMessages) {
        // Link messages
        Map.Entry<Map<CameraMessage, CameraMessage>, List<CameraMessage>> speedPair = linkMessages(speedMessages);

        // List of unprocessed messages to return
        List<CameraMessage> unprocessed = new ArrayList<>(speedPair.getValue());

        // Detect speedfines
        List<Fine> speedFines = speedPair.getKey().entrySet().stream()
                .map(pair -> {
                    try {
                        Optional<Camera> optionalFirstCamera = cameraServiceAdapter.getCamera(pair.getKey().getCameraId());
                        Optional<Camera> optionalSecondCamera = cameraServiceAdapter.getCamera(pair.getValue().getCameraId());
                        if (optionalFirstCamera.isPresent() && optionalSecondCamera.isPresent()) {
                            LinkedList<Camera> cameras = new LinkedList<>() {{
                                offerFirst(optionalFirstCamera.get());
                                offerLast(optionalSecondCamera.get());
                            }};

                            Optional<Licenseplate> optionalLicenseplate = licenseplateServiceAdapter.getLicensePlate(pair.getKey().getLicenseplate());
                            if (optionalLicenseplate.isPresent()) {
                                Licenseplate licenseplate = optionalLicenseplate.get();
                                return fineCalculationService.calcSpeedFine(pair, cameras, licenseplate, speedFineFactorSlow, speedFineFactorFast, paymentDeadlineDays);
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.severe(e.getMessage());
                    }
                    unprocessed.add(pair.getKey());
                    unprocessed.add(pair.getValue());
                    return Optional.empty();
                })
                .filter(Optional::isPresent)
                .map(optional -> (Fine) optional.get())
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
                try {
                    Optional<Camera> optionalCamera = cameraServiceAdapter.getCamera(message.getCameraId());
                    if (optionalCamera.isPresent()) {
                        if (optionalCamera.get().getSegment() != null) {
                            unprocessedMessages.stream()
                                    .filter(m -> {
                                        boolean linked =
                                                m.getCameraId() == optionalCamera.get().getSegment().getConnectedCameraId() &&
                                                        m.getTimestamp().isAfter(message.getTimestamp());

                                        try {
                                            if (m.getLicenseplate() == null && m.getCameraImage() != null) {
                                                Optional<Licenseplate> optionalLicenseplate = licenseplateServiceAdapter.getLicensePlate(m.getCameraImage());
                                                if (optionalLicenseplate.isPresent()) {
                                                    linked &= optionalLicenseplate.get().getPlateId().equalsIgnoreCase(message.getLicenseplate());
                                                } else {
                                                    linked = false;
                                                }
                                            } else {
                                                linked &= m.getLicenseplate().equalsIgnoreCase(message.getLicenseplate());
                                            }
                                        } catch (Exception e) {
                                            LOGGER.severe(e.getMessage());
                                            linked = false;
                                        }

                                        return linked;
                                    })
                                    .findFirst()
                                    .ifPresent(m -> {
                                        if (optionalCamera.get().getSegment() != null) {
                                            linkedMessages.put(message, m);
                                        } else {
                                            linkedMessages.put(m, message);
                                        }
                                        unprocessedMessages.remove(message);
                                        unprocessedMessages.remove(m);
                                    });
                        }
                    }
                } catch (CameraNotFoundException cnfe) {
                    LOGGER.severe(cnfe.getMessage());
                } catch (Exception e) {
                    LOGGER.severe(String.format("Could not link message %s", message));
                }
            }
        }

        return Map.entry(linkedMessages, unprocessedMessages);
    }
}

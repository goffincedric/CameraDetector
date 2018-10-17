package be.kdg.processor.fine.services;

import be.kdg.processor.camera.dom.Camera;
import be.kdg.processor.camera.dom.CameraMessage;
import be.kdg.processor.camera.dom.CameraType;
import be.kdg.processor.camera.services.CameraServiceAdapter;
import be.kdg.processor.fine.FineDetector;
import be.kdg.processor.fine.dom.Fine;
import be.kdg.processor.fine.exceptions.FineException;
import be.kdg.processor.fine.repository.FineRepository;
import be.kdg.processor.licenseplate.dom.Licenseplate;
import be.kdg.processor.licenseplate.services.LicenseplateServiceAdapter;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Cédric Goffin
 * 12/10/2018 15:21
 */
@Service
@Transactional
public class FineService {
    private final CameraServiceAdapter cameraServiceAdapter;
    private final LicenseplateServiceAdapter licenseplateServiceAdapter;

    private final FineRepository fineRepository;
    private final FineDetector fineDetector;

    @Autowired
    public FineService(CameraServiceAdapter cameraServiceAdapter, LicenseplateServiceAdapter licenseplateServiceAdapter, FineRepository fineRepository, FineDetector fineDetector) {
        this.cameraServiceAdapter = cameraServiceAdapter;
        this.licenseplateServiceAdapter = licenseplateServiceAdapter;
        this.fineRepository = fineRepository;
        this.fineDetector = fineDetector;
    }

    public Optional<Fine> getFine(int id) {
        return fineRepository.findById(id);
    }

    public List<Fine> getFinesBetween(LocalDateTime from, LocalDateTime to) throws FineException {
        if (from.isAfter(to)) throw new FineException("From date (" + from + ") is after to date (" + to + ")");
        return fineRepository.findAllByTimestampBetween(from, to);
    }

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

    public Fine save(Fine fine) {
        return fineRepository.save(fine);
    }

    public List<Fine> saveFines(List<Fine> fines) {
        fines = fineRepository.saveAll(fines);
        return fines;
    }

    public List<CameraMessage> processFines(List<CameraMessage> messages) {
        // Filter out speedmessages
        List<CameraMessage> speedMessages = cameraServiceAdapter.getMessagesFromTypes(messages, List.of(CameraType.SPEED, CameraType.SPEED_EMISSION));
        // Filter out emissionmessages
        List<CameraMessage> emissionMessages = cameraServiceAdapter.getMessagesFromTypes(messages, List.of(CameraType.EMISSION, CameraType.SPEED_EMISSION));

        //TODO: init unprocessed messages list with messages that where not speed/emissionmessages
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
        fines = saveFines(fines);

        return unprocessed;
    }

    private Map.Entry<List<Fine>, List<CameraMessage>> processEmissionFines(List<CameraMessage> emissionMessages) {
        // List of unprocessed messages to return
        List<CameraMessage> unprocessed = new ArrayList<>();

        // Process emissionMessages
        List<Fine> duplicateEmissionFines = emissionMessages.stream()
                .map(m -> {
                    Optional<Camera> optionalCamera = cameraServiceAdapter.getCamera(m.getCameraId());
                    if (optionalCamera.isPresent()) {
                        Camera camera = optionalCamera.get();

                        Optional<Licenseplate> optionalLicenseplate = licenseplateServiceAdapter.getLicensePlate(m);
                        if (optionalLicenseplate.isPresent()) {
                            Licenseplate licenseplate = optionalLicenseplate.get();
                            return fineDetector.checkEmissionFine(m, camera, licenseplate);
                        }
                    }
                    unprocessed.add(m);
                    return Optional.empty();
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(f -> (Fine) f)
                .collect(Collectors.toList());

        // Get only 1 fine per licenseplate per day
        List<Fine> distinctEmissionFines = duplicateEmissionFines.stream()
                .distinct()
                .collect(Collectors.toList());

        // Return fines and unprocessed messages
        return Map.entry(distinctEmissionFines, unprocessed);
    }

    private Map.Entry<List<Fine>, List<CameraMessage>> processSpeedingFines(List<CameraMessage> speedMessages) {
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

                        Optional<Licenseplate> optionalLicenseplate = licenseplateServiceAdapter.getLicensePlate(pair.getKey().getLicenseplate());
                        if (optionalLicenseplate.isPresent()) {
                            Licenseplate licenseplate = optionalLicenseplate.get();
                            return fineDetector.checkSpeedFine(pair, camera, licenseplate);
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
                        Optional<CameraMessage> optionalLinkedMessage = unprocessedMessages.stream()
                                .filter(m ->
                                        m.getCameraId() == camera.getSegment().getConnectedCameraId() &&
                                        m.getLicenseplate().equalsIgnoreCase(message.getLicenseplate()) &&
                                        m.getTimestamp().isAfter(message.getTimestamp()))
                                .findFirst();
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

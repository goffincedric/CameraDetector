package be.kdg.processor.processor;

import be.kdg.processor.camera.services.CameraServiceAdapter;
import be.kdg.processor.fine.FineDetector;
import be.kdg.processor.fine.dom.Fine;
import be.kdg.processor.fine.services.FineService;
import be.kdg.processor.camera.dom.Camera;
import be.kdg.processor.camera.dom.CameraMessage;
import be.kdg.processor.licenseplate.services.LicenseplateServiceAdapter;
import be.kdg.processor.licenseplate.misc.CloudALPRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.logging.Logger;

/**
 * @author Cédric Goffin
 * 02/10/2018 13:51
 */

@Component
@EnableScheduling
public class Processor {
    private static final Logger LOGGER = Logger.getLogger(Processor.class.getName());
    private final CameraServiceAdapter cameraServiceAdapter;
    private final LicenseplateServiceAdapter licenseplateServiceAdapter;
    private final FineDetector fineDetector;
    private final FineService fineService;

    private Map<CameraMessage, Integer> messageMap;

    @Value("${processor.retries}")
    private int retries;

    @Autowired
    public Processor(CameraServiceAdapter cameraServiceAdapter, LicenseplateServiceAdapter licenseplateServiceAdapter, FineDetector fineDetector, FineService fineService) {
        this.cameraServiceAdapter = cameraServiceAdapter;
        this.licenseplateServiceAdapter = licenseplateServiceAdapter;
        this.fineDetector = fineDetector;
        this.fineService = fineService;
        this.messageMap = new HashMap<>();
    }

    @Scheduled(fixedRate = 10000)
    public void CheckMessages() {
        if (messageMap.isEmpty()) return;

        // Copy & empty messageMap
        Map<Integer, Map<CameraMessage, Integer>> messagesBuffer = new HashMap<>(messageMap);
        messageMap.clear();



        // Make empty list of fines;
        List<Fine> fines = new ArrayList<>();

        // Link message to camera
        Map<Camera, List<CameraMessage>> messageMap = new HashMap<>();
        messagesBuffer.forEach((message, count) -> {
            if (retries > count) {
                // Get camera from message
                Optional<Camera> optionalCamera = cameraServiceAdapter.getCamera(message.getCameraId());
                if (optionalCamera.isPresent()) {
                    Camera camera = optionalCamera.get();

                    if (messageMap.containsKey(camera)) {
                        messageMap.get(camera).add(message);
                    } else {
                        messageMap.put(camera, List.of(message));
                    }
                    return;
                }

                // Failed to process messages
                LOGGER.warning("Could not process message '" + message + "'!");
                messageMap.put(message, messages.get(message) + 1);
            } else {
                LOGGER.warning("Logging message '" + message + "' to file because it failed to process more than " + retries + " times!");
                //TODO: Log failed messages to file
            }
        });

        List<Camera> cameras = cameraServiceAdapter.getAllCameras();

//        messageMap.forEach((camera, messageList) -> {
//            // Get licenseplate from message
//            Licenseplate licenseplate;
//            Optional<Licenseplate> optionalLicenseplate = Optional.empty();
//            if (message.getCameraImage() != null) {
//                String plate = cloudALPRService.getLicenseplate(message.getCameraImage());
//                optionalLicenseplate = licenseplateServiceAdapter.getLicensePlate(plate);
//            } else if (message.getLicenseplate() != null) {
//                optionalLicenseplate = licenseplateServiceAdapter.getLicensePlate(message.getLicenseplate());
//            }
//
//
//            if (optionalLicenseplate.isPresent()) {
//                licenseplate = optionalLicenseplate.get();
//
//                //TODO
//                        /*
//                        // Check if message needs additional message for speed calculation
//                        if (camera.getSegment() != null) {
//                            List<CameraMessage> linkedMessages;
//
//                            Optional<CameraMessage> linkedMessage = messages.keySet().stream()
//                                    .filter(m -> m.getCameraId() == camera.getSegment().getConnectedCameraId() && m.getLicenseplate().equalsIgnoreCase(licenseplate.getPlateId()))
//                                    .findFirst();
//
//                            if (linkedMessage.isPresent()) {
//                                linkedMessages = List.of(message, linkedMessage.get());
//                                fines = fineDetector.checkFines(linkedMessages, camera, licenseplate);
//                            }
//                        }*/
//
//            }
//
//            fines.addAll(fineDetector.)
//        });

        // Log fines and write to db
        fines.forEach(fine -> LOGGER.info("Car with license plate " + fine.getLicenseplate().getPlateId() + " got fined with fine " + fine));
        if (!fines.isEmpty()) checkAndSaveFines(fines);
    }

    private List<Fine> checkAndSaveFines(List<Fine> fines) {
        // TODO: Check for duplicate messages (or 2 emissionmessages for 1 day for 1 licenseplate)
        return fineService.saveFines(fines);
    }

    public void reportMessage(CameraMessage message) {
            if (messageMap.containsKey(message.getCameraId())) {
                messageMap.get(message.getCameraId()).put(message, 0);
            } else {
                messageMap.put(message.getCameraId(), Map.of(message, 0));
            }
        }
    }
}

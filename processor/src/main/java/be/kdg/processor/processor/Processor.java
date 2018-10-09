package be.kdg.processor.processor;

import be.kdg.processor.camera.services.CameraServiceAdapter;
import be.kdg.processor.fine.finedetection.FineDetector;
import be.kdg.processor.fine.Fine;
import be.kdg.processor.licenseplate.Licenseplate;
import be.kdg.processor.camera.Camera;
import be.kdg.processor.camera.CameraMessage;
import be.kdg.processor.licenseplate.services.LicenseplateServiceAdapter;
import be.kdg.processor.processor.services.CloudALPRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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
    private final CloudALPRService cloudALPRService;
    private final FineDetector fineDetector;

    private List<CameraMessage> messageList;

    @Autowired
    public Processor(CameraServiceAdapter cameraServiceAdapter, LicenseplateServiceAdapter licenseplateServiceAdapter, CloudALPRService cloudALPRService, FineDetector fineDetector) {
        this.cameraServiceAdapter = cameraServiceAdapter;
        this.licenseplateServiceAdapter = licenseplateServiceAdapter;
        this.cloudALPRService = cloudALPRService;
        this.fineDetector = fineDetector;
        this.messageList = new ArrayList<>();
    }

    @Scheduled(fixedRate = 10000)
    public void CheckMessage() {
        if (messageList.isEmpty()) return;

        // Copy & empty buffer list
        List<CameraMessage> messages = new ArrayList<>(messageList);
        messageList.clear();

        messages.forEach(message -> {
            Camera camera = cameraServiceAdapter.getCamera(message.getCameraId());
            if (camera != null) {
                Licenseplate licenseplate = null;

                if (message.getCameraImage() != null) {
                    String plate = cloudALPRService.getLicenseplate(message.getCameraImage());
                    licenseplate = licenseplateServiceAdapter.getLicensePlate(plate);
                } else if (message.getLicenseplate() != null) {
                    licenseplate = licenseplateServiceAdapter.getLicensePlate(message.getLicenseplate());
                }

                List<Fine> fines = new ArrayList<>();

                switch (camera.getCameraType()) {
                    case EMISSION:
                        Fine emissionFine = fineDetector.checkEmissionFine(message, camera, licenseplate);
                        if (emissionFine != null) fines.add(emissionFine);
                        break;
                    case SPEED:
                        Fine speedFine = fineDetector.checkSpeedFine(message, camera, licenseplate);
                        if (speedFine != null) fines.add(speedFine);
                        break;
                    case SPEED_EMISSION:
                        List<Fine> fineList = fineDetector.checkFines(message, camera, licenseplate);
                        if (fineList != null) fines.addAll(fineList);
                        break;
                }

                //TODO: write fine(s) to db
                fines.forEach(fine -> LOGGER.info("Car with license plate " + licenseplate.getPlateId() + " got fined with fine " + fine));
            }
        });
    }

    public boolean reportMessage(CameraMessage message) {
        return messageList.add(message);
    }
}

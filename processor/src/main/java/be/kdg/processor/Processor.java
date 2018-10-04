package be.kdg.processor;

import be.kdg.processor.finedetection.FineDetector;
import be.kdg.processor.model.fine.Fine;
import be.kdg.processor.model.licenseplate.Licenseplate;
import be.kdg.processor.services.InformationServiceAdapter;
import be.kdg.processor.model.camera.Camera;
import be.kdg.processor.model.camera.CameraMessage;
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
    private final InformationServiceAdapter informationServiceAdapter;
    private final FineDetector fineDetector;

    private List<CameraMessage> messageList;

    @Autowired
    public Processor(InformationServiceAdapter informationServiceAdapter, FineDetector fineDetector) {
        this.informationServiceAdapter = informationServiceAdapter;
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
            Camera camera = informationServiceAdapter.getCamera(message.getId());
            if (camera != null) {
                Licenseplate licenseplate = informationServiceAdapter.getLicensePlate(message.getLicenseplate());
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
                fines.forEach(fine -> {
                    LOGGER.info("Car with license plate " + licenseplate.getPlateId() + " got fined with fine " + fine);
                });
            }
        });
    }

    public boolean reportMessage(CameraMessage message) {
        return messageList.add(message);
    }
}

package be.kdg.processor.finedetection;

import be.kdg.processor.model.camera.Camera;
import be.kdg.processor.model.camera.CameraMessage;
import be.kdg.processor.model.fine.Fine;
import be.kdg.processor.model.licenseplate.Licenseplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Cédric Goffin
 * 04/10/2018 16:18
 */
@Component
public class FineDetector {
    public Fine checkEmissionFine(CameraMessage message, Camera camera, Licenseplate licenseplate) {
        //TODO
        return null;
    }

    public Fine checkSpeedFine(CameraMessage message, Camera camera, Licenseplate licenseplate) {
        //TODO

        return null;
    }

    public List<Fine> checkFines(CameraMessage message, Camera camera, Licenseplate licenseplate) {
        //TODO
        return null;
    }
}

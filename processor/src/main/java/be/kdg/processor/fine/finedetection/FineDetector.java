package be.kdg.processor.fine.finedetection;

import be.kdg.processor.camera.dom.Camera;
import be.kdg.processor.camera.dom.CameraMessage;
import be.kdg.processor.fine.dom.Fine;
import be.kdg.processor.licenseplate.dom.Licenseplate;
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

package be.kdg.processor.Services;

import be.kdg.processor.model.camera.Camera;
import be.kdg.processor.model.licenseplate.Licenseplate;

/**
 * @author Cédric Goffin
 * 03/10/2018 18:38
 */
public interface FineDetection {
    Licenseplate getLicensePlate(String licensePlateId);
    Camera getCamera(int cameraId);
}

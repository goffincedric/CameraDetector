package be.kdg.processor.Services;

import be.kdg.processor.misc.JSONUtils;
import be.kdg.processor.model.camera.Camera;
import be.kdg.processor.model.licenseplate.Licenseplate;
import be.kdg.sa.services.CameraServiceProxy;
import be.kdg.sa.services.LicensePlateServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Cédric Goffin
 * 03/10/2018 18:41
 */
@Component
public class FineDetectionService implements FineDetection {
    private static final Logger LOGGER = Logger.getLogger(FineDetectionService.class.getName());

    @Autowired
    private CameraServiceProxy cameraServiceProxy;
    @Autowired
    private LicensePlateServiceProxy licensePlateServiceProxy;

    @Override
    public Licenseplate getLicensePlate(String licensePlateId) {
        Licenseplate licenseplate = null;
        try {
            licenseplate = JSONUtils.convertJSONToObject(licensePlateServiceProxy.get(licensePlateId), Licenseplate.class);
        } catch (IOException e) {
            LOGGER.severe("Could not find license plate with id: " + licensePlateId);
            e.printStackTrace();
        }
        return licenseplate;
    }

    @Override
    public Camera getCamera(int cameraId) {
        Camera camera = null;
        try {
            camera = JSONUtils.convertJSONToObject(cameraServiceProxy.get(cameraId), Camera.class);
        } catch (IOException e) {
            LOGGER.severe("Could not find camera with id: " + cameraId);
            e.printStackTrace();
        }
        return camera;
    }
}

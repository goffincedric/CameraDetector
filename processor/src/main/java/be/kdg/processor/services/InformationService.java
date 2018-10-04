package be.kdg.processor.services;

import be.kdg.processor.utils.JSONUtils;
import be.kdg.processor.model.camera.Camera;
import be.kdg.processor.model.camera.CameraType;
import be.kdg.processor.model.licenseplate.Licenseplate;
import be.kdg.sa.services.CameraNotFoundException;
import be.kdg.sa.services.CameraServiceProxy;
import be.kdg.sa.services.LicensePlateNotFoundException;
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
public class InformationService implements InformationServiceAdapter {
    private static final Logger LOGGER = Logger.getLogger(InformationService.class.getName());

    private CameraServiceProxy cameraServiceProxy;
    private LicensePlateServiceProxy licensePlateServiceProxy;

    @Autowired
    public InformationService(CameraServiceProxy cameraServiceProxy, LicensePlateServiceProxy licensePlateServiceProxy) {
        this.cameraServiceProxy = cameraServiceProxy;
        this.licensePlateServiceProxy = licensePlateServiceProxy;
    }

    @Override
    public Licenseplate getLicensePlate(String licensePlateId) {
        Licenseplate licenseplate = null;
        try {
            licenseplate = JSONUtils.convertJSONToObject(licensePlateServiceProxy.get(licensePlateId), Licenseplate.class);
        } catch (IOException e) {
            LOGGER.severe("Unable to deserialize licenseplate with id: " + licensePlateId);
            e.printStackTrace();
        } catch (LicensePlateNotFoundException lnfe) {
            LOGGER.severe("Could not find license plate with id: " + licensePlateId);
        }
        return licenseplate;
    }

    @Override
    public Camera getCamera(int cameraId) {
        Camera camera = null;
        try {
            camera = JSONUtils.convertJSONToObject(cameraServiceProxy.get(cameraId), Camera.class);
            if (camera != null) {
                if (camera.getSegment() == null) {
                    camera.setCameraType(CameraType.EMISSION);
                } else if (camera.getEuroNorm() == 0) {
                    camera.setCameraType(CameraType.SPEED);
                } else {
                    camera.setCameraType(CameraType.SPEED_EMISSION);
                }
            }
        } catch (IOException e) {
            LOGGER.severe("Unable to deserialize camera with id: " + cameraId);
            e.printStackTrace();
        } catch (CameraNotFoundException cnfe) {
            LOGGER.severe("Could not find camera with id: " + cameraId);
        }
        return camera;
    }
}

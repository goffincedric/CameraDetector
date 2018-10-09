package be.kdg.processor.camera.services;

import be.kdg.processor.camera.Camera;
import be.kdg.processor.camera.CameraType;
import be.kdg.processor.utils.JSONUtils;
import be.kdg.sa.services.CameraNotFoundException;
import be.kdg.sa.services.CameraServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Cédric Goffin
 * 09/10/2018 13:38
 */
@Component
public class CameraServiceAdapter {
    private static final Logger LOGGER = Logger.getLogger(CameraServiceAdapter.class.getName());

    private final CameraServiceProxy cameraServiceProxy;

    @Autowired
    public CameraServiceAdapter(CameraServiceProxy cameraServiceProxy) {
        this.cameraServiceProxy = cameraServiceProxy;
    }

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

package be.kdg.processor.camera.services;

import be.kdg.processor.camera.Camera;
import be.kdg.processor.camera.CameraType;
import be.kdg.processor.camera.repository.CameraRepository;
import be.kdg.processor.utils.JSONUtils;
import be.kdg.sa.services.CameraNotFoundException;
import be.kdg.sa.services.CameraServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Cédric Goffin
 * 09/10/2018 13:38
 */
@Service
public class CameraServiceAdapter {
    private static final Logger LOGGER = Logger.getLogger(CameraServiceAdapter.class.getName());

    private final CameraServiceProxy cameraServiceProxy;
    private final CameraRepository cameraRepository;

    @Autowired
    public CameraServiceAdapter(CameraServiceProxy cameraServiceProxy, CameraRepository cameraRepository) {
        this.cameraServiceProxy = cameraServiceProxy;
        this.cameraRepository = cameraRepository;
    }

    public Camera getCamera(int cameraId) {
        Camera camera = null;

        if (cameraRepository.existsByCameraId(cameraId)) {
            camera = cameraRepository.findByCameraId(cameraId).get();
        } else {
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
        }

        return camera;
    }

    public List<Camera> getAllCameras() {
        return cameraRepository.findAll();
    }

    public List<Camera> getAllCameras(int id) {
        return cameraRepository.findAll();
    }

    public Camera updateCamera(Camera camera, int cameraId) {
        if (!cameraRepository.existsByCameraId(cameraId)) throw new CameraNotFoundException(cameraId);
        return cameraRepository.save(camera);
    }

    public Camera createCamera(Camera camera) {
        return cameraRepository.save(camera);
    }
}

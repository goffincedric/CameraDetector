package be.kdg.processor.camera.services;

import be.kdg.processor.camera.dom.Camera;
import be.kdg.processor.camera.dom.CameraMessage;
import be.kdg.processor.camera.dom.CameraType;
import be.kdg.processor.camera.repository.CameraRepository;
import be.kdg.processor.utils.JSONUtils;
import be.kdg.sa.services.CameraNotFoundException;
import be.kdg.sa.services.CameraServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Cédric Goffin
 * 09/10/2018 13:38
 */
@Service
@Transactional
public class CameraServiceAdapter {
    private static final Logger LOGGER = Logger.getLogger(CameraServiceAdapter.class.getName());

    private final CameraServiceProxy cameraServiceProxy;
    private final CameraRepository cameraRepository;

    @Autowired
    public CameraServiceAdapter(CameraServiceProxy cameraServiceProxy, CameraRepository cameraRepository) {
        this.cameraServiceProxy = cameraServiceProxy;
        this.cameraRepository = cameraRepository;
    }

    public Optional<Camera> getCamera(int cameraId) {
        Optional<Camera> optionalCamera;

        if (cameraRepository.existsByCameraId(cameraId)) {
            optionalCamera = cameraRepository.findByCameraId(cameraId);
        } else {
            try {
                optionalCamera = JSONUtils.convertJSONToObject(cameraServiceProxy.get(cameraId), Camera.class);
                if (optionalCamera.isPresent()) {
                    List<Camera> cameras = getAllCameras();
                    Camera camera = optionalCamera.get();
                    if (camera.getSegment() == null && cameras.stream().noneMatch(c -> c.getSegment() != null && c.getSegment().getConnectedCameraId() == camera.getCameraId())) {
                        camera.setCameraType(CameraType.EMISSION);
                    } else if (camera.getEuroNorm() == 0) {
                        camera.setCameraType(CameraType.SPEED);
                    } else {
                        camera.setCameraType(CameraType.SPEED_EMISSION);
                    }
                    optionalCamera = Optional.of(createCamera(camera));
                }
            } catch (IOException e) {
                LOGGER.severe("Unable to deserialize camera with id: " + cameraId);
                optionalCamera = Optional.empty();
            } catch (CameraNotFoundException cnfe) {
                LOGGER.severe("Could not find camera with id: " + cameraId);
                optionalCamera = Optional.empty();
            }
        }

        return optionalCamera;
    }

    public List<Camera> getAllCameras() {
        List<Camera> repoCameras = cameraRepository.findAll();
        List<Camera> proxyCameras = new ArrayList<>();

        int counter = 1;
        boolean next = true;
        do {
            try {
                JSONUtils.convertJSONToObject(cameraServiceProxy.get(counter), Camera.class).ifPresent(proxyCameras::add);
                counter++;
            } catch (Exception e) {
                next = false;
            }
        } while (next);

        return
                proxyCameras.stream()
                .map(c -> {
                    if (repoCameras.contains(c))
                        return repoCameras.get(repoCameras.indexOf(c));
                    else
                        return c;
                }).collect(Collectors.toList());
    }

    public List<Camera> getAllCamerasByEuronorm(int euronorm) {
        return cameraRepository.findAllByEuroNorm(euronorm);
    }

    public Camera updateCamera(Camera camera, int cameraId) {
        if (!cameraRepository.existsByCameraId(cameraId)) throw new CameraNotFoundException(cameraId);
        return cameraRepository.save(camera);
    }

    public Camera createCamera(Camera camera) {
//        if (camera.getSegment() != null) {
//            Optional<Camera> optionalCamera = getCamera(camera.getSegment().getConnectedCameraId());
//            optionalCamera.ifPresent(cam -> {
//                camera.getSegment().setCamera(cam);
//                if (cam.getSegment() != null && cam.getSegment().getConnectedCameraId() == cam.getCameraId()) {
//                    cam.getSegment().setCamera(camera);
//                }
//            });
//        }
        //TODO: Werkt niet
        return cameraRepository.saveAndFlush(camera);
    }

    public List<CameraMessage> getMessagesFromTypes(List<CameraMessage> messages, List<CameraType> cameraTypes) {
        return messages.stream()
                .filter(m -> {
                    Optional<Camera> optionalCamera = getCamera(m.getCameraId());
                    if (optionalCamera.isPresent()) {
                        Camera camera = optionalCamera.get();
                        return cameraTypes.contains(camera.getCameraType());
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }
}

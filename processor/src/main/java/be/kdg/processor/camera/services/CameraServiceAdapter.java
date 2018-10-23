package be.kdg.processor.camera.services;

import be.kdg.processor.camera.dom.Camera;
import be.kdg.processor.camera.dom.CameraMessage;
import be.kdg.processor.camera.dom.CameraType;
import be.kdg.processor.camera.repository.CameraRepository;
import be.kdg.processor.utils.JSONUtils;
import be.kdg.sa.services.CameraNotFoundException;
import be.kdg.sa.services.CameraServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Service used to manipulate Camera information from the H2 in-memory database or from an external CameraServiceProxy.
 *
 * @author Cédric Goffin
 * @see CameraServiceProxy
 * @see CameraRepository
 */
@Service
@Transactional
@EnableCaching
public class CameraServiceAdapter {
    private static final Logger LOGGER = Logger.getLogger(CameraServiceAdapter.class.getName());

    private final CameraServiceProxy cameraServiceProxy;
    private final CameraRepository cameraRepository;

    /**
     * Constructor used by Spring framework to initialize the service as a bean
     *
     * @param cameraServiceProxy is an external CameraServiceProxy which can be contacted to get information about Cameras
     * @param cameraRepository   is the repository that has access to the H2 in-memory database
     */
    @Autowired
    public CameraServiceAdapter(CameraServiceProxy cameraServiceProxy, CameraRepository cameraRepository) {
        this.cameraServiceProxy = cameraServiceProxy;
        this.cameraRepository = cameraRepository;
    }

    /**
     * Gets information about a camera.
     *
     * @param cameraId is the camera id
     * @return an Optional Camera. Is empty when no Camera could be found or when an error occurred.
     */
    @Cacheable("camera")
    public Optional<Camera> getCamera(int cameraId) {
        Optional<Camera> optionalCamera = cameraRepository.findByCameraId(cameraId);

        if (!optionalCamera.isPresent()) {
            try {
                optionalCamera = JSONUtils.convertJSONToObject(cameraServiceProxy.get(cameraId), Camera.class);
                if (optionalCamera.isPresent()) {
                    List<Camera> cameras = getAllCameras();
                    Camera camera = optionalCamera.get();
                    optionalCamera = cameras.stream().filter(c -> c.getCameraId() == camera.getCameraId()).findFirst();
                }
            } catch (IOException e) {
                LOGGER.severe(String.format("Unable to deserialize camera with id: %d", cameraId));
                optionalCamera = Optional.empty();
            } catch (CameraNotFoundException cnfe) {
                LOGGER.severe(String.format("Could not find camera with id: %d", cameraId));
                optionalCamera = Optional.empty();
            }
        }

        return optionalCamera;
    }

    /**
     * Gets a list of all Cameras in both the repository or CameraServiceProxy
     *
     * @return a list of all known Cameras
     */
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
                            else if (c.getSegment() == null && proxyCameras.stream().noneMatch(ca -> ca.getSegment() != null && ca.getSegment().getConnectedCameraId() == c.getCameraId())) {
                                c.setCameraType(CameraType.EMISSION);
                            } else if (c.getEuroNorm() == 0) {
                                c.setCameraType(CameraType.SPEED);
                            } else {
                                c.setCameraType(CameraType.SPEED_EMISSION);
                            }
                            return createCamera(c);
                        }).collect(Collectors.toList());
    }

    /**
     * Gets a list of all Cameras that have a certain euronorm
     *
     * @param euronorm is the minimum euronorm of a camera
     * @return a list of all known Cameras with a certain euronorm
     */
    public List<Camera> getAllCamerasByEuronorm(int euronorm) {
        return cameraRepository.findAllByEuroNorm(euronorm);
    }

    /**
     * Updates a Camera from the repository
     *
     * @param camera   is the updated Camera
     * @param cameraId is the id of the Camera that needs to be updated
     * @return the updated camera from the repository
     */
    public Camera updateCamera(Camera camera, int cameraId) {
        if (!cameraRepository.existsByCameraId(cameraId)) throw new CameraNotFoundException(cameraId);
        return cameraRepository.save(camera);
    }

    /**
     * Creates a new camera in the repository
     *
     * @param camera is the camera to persist to the database
     * @return the persisted camera from the repository
     */
    public Camera createCamera(Camera camera) {
        return cameraRepository.saveAndFlush(camera);
    }

    /**
     * Method used to filter out messages from a certain/multiple camera types.
     *
     * @param messages    is a list of messages that need to be filtered
     * @param cameraTypes is a list of CameraTypes used to filter out the messages
     * @return a list of messages that where generated by a Camera of one of the supplied types
     */
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

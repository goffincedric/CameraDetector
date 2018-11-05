package be.kdg.processor.camera.services;

import be.kdg.processor.camera.dom.Camera;
import be.kdg.processor.camera.dom.CameraLocation;
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
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Service used to manipulate Camera information from the H2 in-memory database or from an external CameraServiceProxy.
 *
 * @author C&eacute;dric Goffin
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
     * @throws CameraNotFoundException when no Camera could be found with the supplied id
     */
    @Cacheable("camera")
    public Optional<Camera> getCamera(int cameraId) throws CameraNotFoundException {
        Optional<Camera> optionalCamera = cameraRepository.findByCameraId(cameraId);

        if (!optionalCamera.isPresent()) {
            try {
                optionalCamera = JSONUtils.convertJSONToObject(cameraServiceProxy.get(cameraId), Camera.class);
                if (optionalCamera.isPresent()) {
                    List<Camera> cameras = getAllCameras();
                    Camera camera = optionalCamera.get();
                    return cameras.stream().filter(c -> c.getCameraId() == camera.getCameraId()).findFirst();
                }
            } catch (IOException e) {
                LOGGER.severe(String.format("Unable to deserialize camera with id: %d", cameraId));
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

        return proxyCameras.stream()
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
     * Method that calculates the center coordinates of all current cameras
     *
     * @return a Map entry with the center latitude as key and center longitude as value
     */
    public Map.Entry<Double, Double> getCenterCoordsOfAllCameras() {
        List<CameraLocation> cameraLocations = getAllCameras().stream()
                .map(Camera::getLocation)
                .collect(Collectors.toList());

        double lat = 0D;
        double lon = 0D;
        int coordsCount = cameraLocations.size();
        for (CameraLocation cameraLocation : cameraLocations) {
            lat += cameraLocation.getLatitude();
            lon += cameraLocation.getLongitude();
        }

        lat = lat / coordsCount;
        lon = lon / coordsCount;

        return Map.entry(lat, lon);
    }

    /**
     * Method that returns a list with all CameraLocations for each Fine in the database
     *
     * @return a list of CameraLocation objects
     */
    public Map<CameraLocation, Integer> getFineCountByCameraLocation() {
        return getAllCameras().stream()
                .collect(Collectors.toMap(Camera::getLocation, camera -> camera.getFines().size()));
    }

    /**
     * @return a geojson formatted string with all CameraLocations for each Fine in the database
     */
    public String getAllFineCameraLocationsGeoJson() {
        Map<CameraLocation, Integer> dataSet = getFineCountByCameraLocation();

        StringBuilder geoJsonBuilder = new StringBuilder("{\"features\": [");
        dataSet.forEach((loc, count) -> {
            geoJsonBuilder.append("{\"type\": \"Feature\",\"properties\": {");
            geoJsonBuilder.append(String.format(Locale.US, "\"count\": %d},\"geometry\": {\"type\": \"Point\",\"coordinates\": [%f, %f]}},", count, loc.getLongitude(), loc.getLatitude()));
        });
        geoJsonBuilder.deleteCharAt(geoJsonBuilder.length() - 1).append("],\"type\": \"FeatureCollection\"}");
        return geoJsonBuilder.toString();
    }

    /**
     * Creates a new camera in the repository
     *
     * @param camera is the camera to persist to the database
     * @return the persisted camera from the repository
     */
    public Camera createCamera(Camera camera) {
        return cameraRepository.save(camera);
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
                    try {
                        Optional<Camera> optionalCamera = getCamera(m.getCameraId());
                        if (optionalCamera.isPresent()) {
                            Camera camera = optionalCamera.get();
                            return cameraTypes.contains(camera.getCameraType());
                        }
                    } catch (CameraNotFoundException cnfe) {
                        LOGGER.severe(cnfe.getMessage());
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }
}

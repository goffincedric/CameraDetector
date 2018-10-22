package be.kdg.processor.camera.controllers.rest;

import be.kdg.processor.camera.dom.Camera;
import be.kdg.processor.camera.dto.CameraDTO;
import be.kdg.processor.camera.services.CameraServiceAdapter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Rest controller for Camera package. Mapped to listen to requests on /api/camera
 *
 * @author Cédric Goffin
 */
@RestController
@RequestMapping("/api/camera")
public class CameraRestController {
    private final CameraServiceAdapter cameraServiceAdapter;
    private final ModelMapper modelMapper;

    /**
     * CameraRestController constructor. Autowired via Spring.
     *
     * @param cameraServiceAdapter is the service for the Camera package
     * @param modelMapper          is a mapper that most methods use to map an object to DTO and vice versa
     */
    @Autowired
    public CameraRestController(CameraServiceAdapter cameraServiceAdapter, ModelMapper modelMapper) {
        this.cameraServiceAdapter = cameraServiceAdapter;
        this.modelMapper = modelMapper;
    }

    /**
     * Method that handles incoming GET requests from /api/camera/findall.
     *
     * @return a list of CameraDTO's, wrapped in a ResponseEntity object
     */
    @GetMapping("/findall")
    public ResponseEntity<List<CameraDTO>> findAll() {
        List<CameraDTO> cameraDTOS = cameraServiceAdapter.getAllCameras().stream()
                .map(c -> modelMapper.map(c, CameraDTO.class))
                .collect(Collectors.toList());

        if (cameraDTOS.isEmpty()) return new ResponseEntity<>(cameraDTOS, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(
                cameraDTOS,
                HttpStatus.OK);
    }

    /**
     * Method that handles incoming GET requests from /api/camera/findbyid/{id}
     *
     * @param id a camera id
     * @return a CameraDTO object, wrapped in a ResponseEntity object
     * @throws Exception when a Camera cannot be found or cannot be mapped to a DTO
     */
    @GetMapping("/findbyid/{id}")
    public ResponseEntity<CameraDTO> findById(@PathVariable int id) throws Exception {
        Optional<Camera> optionalCamera = cameraServiceAdapter.getCamera(id);
        if (optionalCamera.isPresent()) {
            return new ResponseEntity<>(
                    modelMapper.map(optionalCamera.get(), CameraDTO.class),
                    HttpStatus.OK
            );
        }
        throw new Exception("Camera with id " + id + " could not be found!");
    }

    /**
     * Method that handles incoming GET requests from /api/camera/findbyeuronorm/{euronorm}
     *
     * @param euronorm a camera euronorm
     * @return a list of CameraDTO's, wrapped in a ResponseEntity object
     */
    @GetMapping("/findbyeuronorm/{euronorm}")
    public ResponseEntity<List<CameraDTO>> fetchDataByLastName(@PathVariable int euronorm) {
        List<CameraDTO> cameraDTOS = cameraServiceAdapter.getAllCamerasByEuronorm(euronorm).stream()
                .map(c -> modelMapper.map(c, CameraDTO.class))
                .collect(Collectors.toList());

        if (cameraDTOS.isEmpty()) return new ResponseEntity<>(cameraDTOS, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(
                cameraDTOS,
                HttpStatus.OK);
    }
}
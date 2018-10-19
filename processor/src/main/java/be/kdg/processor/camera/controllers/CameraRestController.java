package be.kdg.processor.camera.controllers;

import be.kdg.processor.camera.dto.CameraDTO;
import be.kdg.processor.camera.dom.Camera;
import be.kdg.processor.camera.services.CameraServiceAdapter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/camera")
public class CameraRestController {
    private final CameraServiceAdapter cameraServiceAdapter;
    private final ModelMapper modelMapper;

    @Autowired
    public CameraRestController(CameraServiceAdapter cameraServiceAdapter, ModelMapper modelMapper) {
        this.cameraServiceAdapter = cameraServiceAdapter;
        this.modelMapper = modelMapper;
    }

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
package be.kdg.processor.camera.controllers;

import be.kdg.processor.camera.dom.Camera;
import be.kdg.processor.camera.dom.CameraLocation;
import be.kdg.processor.camera.dom.CameraType;
import be.kdg.processor.camera.dom.Segment;
import be.kdg.processor.camera.services.CameraServiceAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cameras")
public class CameraRestController {
    private final CameraServiceAdapter cameraServiceAdapter;

    @Autowired
    public CameraRestController(CameraServiceAdapter cameraServiceAdapter) {
        this.cameraServiceAdapter = cameraServiceAdapter;
    }

    @RequestMapping("/init")
    public String process(){
        cameraServiceAdapter.createCamera(new Camera(1, new CameraLocation(1D, 1D), new Segment(2, 200, 120), 4, CameraType.SPEED_EMISSION));
        return "Done";
    }
       
    @RequestMapping("/findall")
    public String findAll(){
        String result = "";
           
        for(Camera camera : cameraServiceAdapter.getAllCameras()){
            result += camera.toString() + "</br>";
        }
           
        return result;
    }
       
    @RequestMapping("/findbyid")
    public String findById(@RequestParam("id") int id){
        String result = "";
        result = cameraServiceAdapter.getCamera(id).toString();
        return result;
    }
       
    @RequestMapping("/findbylastname")
    public String fetchDataByLastName(@RequestParam("id") int id){
        String result = "";
           
        for(Camera camera: cameraServiceAdapter.getAllCameras(id)){
            result += camera.toString() + "</br>";
        }
           
        return result;
    }
}
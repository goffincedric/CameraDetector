package be.kdg.processor.controller;

import be.kdg.processor.camera.Camera;
import be.kdg.processor.camera.CameraLocation;
import be.kdg.processor.camera.CameraType;
import be.kdg.processor.camera.Segment;
import be.kdg.processor.camera.repository.CameraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerRestController {
    @Autowired
    CameraRepository repository;
       
    @RequestMapping("/save")
    public String process(){
        repository.save(new Camera(1, new CameraLocation(1D, 1D), new Segment(2, 200, 120), 4, CameraType.SPEED_EMISSION));
        return "Done";
    }
       
    @RequestMapping("/findall")
    public String findAll(){
        String result = "";
           
        for(Camera camera : repository.findAll()){
            result += camera.toString() + "</br>";
        }
           
        return result;
    }
       
    @RequestMapping("/findbyid")
    public String findById(@RequestParam("id") int id){
        String result = "";
        result = repository.findByCameraId(id).toString();
        return result;
    }
       
    @RequestMapping("/findbylastname")
    public String fetchDataByLastName(@RequestParam("id") int id){
        String result = "";
           
        for(Camera camera: repository.findAllByCameraId(id)){
            result += camera.toString() + "</br>";
        }
           
        return result;
    }
}
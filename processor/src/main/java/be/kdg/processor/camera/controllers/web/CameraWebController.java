package be.kdg.processor.camera.controllers.web;

import be.kdg.processor.camera.services.CameraServiceAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * A web controller for the Camera package.
 *
 * @author Cédric Goffin
 */
@Controller
@RequestMapping("/camera")
public class CameraWebController {
    private final CameraServiceAdapter cameraServiceAdapter;

    @Autowired
    public CameraWebController(CameraServiceAdapter cameraServiceAdapter) {
        this.cameraServiceAdapter = cameraServiceAdapter;
    }

    /**
     * Listens to requests made on the /camera/heatmap url
     *
     * @return a ModelAndView with the current status of the processor
     */
    @GetMapping(value = "/heatmap")
    public ModelAndView heatmap() {
        ModelAndView mav = new ModelAndView("heatmap");

        Map.Entry<Double, Double> centerCoords = cameraServiceAdapter.getCenterCoordsOfAllCameras();
        mav.addObject("mapCenterLat", centerCoords.getKey());
        mav.addObject("mapCenterLon", centerCoords.getValue());
        mav.addObject("geoJsonData", cameraServiceAdapter.getAllFineCameraLocationsGeoJson());

        return mav;
    }
}

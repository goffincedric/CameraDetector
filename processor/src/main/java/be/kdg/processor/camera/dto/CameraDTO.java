package be.kdg.processor.camera.dto;

import be.kdg.processor.camera.dom.CameraLocation;
import be.kdg.processor.camera.dom.CameraType;
import be.kdg.processor.camera.dom.Segment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO for the Camera class, used to return information about a Camera object.
 *
 * @author C&eacute;dric Goffin
 * @see be.kdg.processor.camera.dom.Camera
 * @see be.kdg.processor.camera.controllers.rest.CameraRestController
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CameraDTO {
    private int cameraId;
    private CameraLocation location;
    private Segment segment;
    private int euroNorm;
    private CameraType cameraType;
}

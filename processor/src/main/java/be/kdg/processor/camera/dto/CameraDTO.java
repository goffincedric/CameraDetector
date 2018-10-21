package be.kdg.processor.camera.dto;

import be.kdg.processor.camera.dom.CameraLocation;
import be.kdg.processor.camera.dom.CameraType;
import be.kdg.processor.camera.dom.Segment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Cédric Goffin
 * 19/10/2018 19:46
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

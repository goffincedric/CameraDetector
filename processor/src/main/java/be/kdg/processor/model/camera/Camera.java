package be.kdg.processor.model.camera;

import lombok.*;

/**
 * @author Cédric Goffin
 * 01/10/2018 14:03
 */

@Getter
@Setter(AccessLevel.NONE)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Camera {
    private int cameraId;
    private CameraLocation location;
    private Segment segment;
    private int euroNorm;
    private CameraType cameraType;

    public Camera(int cameraId, CameraLocation location, Segment segment) {
        this.cameraId = cameraId;
        this.location = location;
        this.segment = segment;
    }

    public Camera(int cameraId, CameraLocation location, int euroNorm) {
        this.cameraId = cameraId;
        this.location = location;
        this.euroNorm = euroNorm;
    }

    public void setCameraType(CameraType cameraType) {
        this.cameraType = cameraType;
    }
}

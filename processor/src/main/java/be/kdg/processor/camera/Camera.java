package be.kdg.processor.camera;

import lombok.*;

import javax.persistence.*;

/**
 * @author Cédric Goffin
 * 01/10/2018 14:03
 */

@Getter
@Setter(AccessLevel.NONE)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "tblCamera")
public class Camera {
    @Id
    @Column(name = "id")
    private int cameraId;
    @OneToOne(targetEntity = CameraLocation.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private CameraLocation location;
    @OneToOne(targetEntity = Segment.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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

    @Override
    public String toString() {
        return "Camera{" +
                "cameraId=" + cameraId +
                ", location=" + location +
                ", segment=" + segment +
                ", euroNorm=" + euroNorm +
                ", cameraType=" + cameraType +
                '}';
    }
}

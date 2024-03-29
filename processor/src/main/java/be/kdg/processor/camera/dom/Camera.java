package be.kdg.processor.camera.dom;

import be.kdg.processor.fine.dom.Fine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class that holds information about a Camera. Gets stored in an H2 in-memory database in a table named 'tblCamera'.
 *
 * @author C&eacute;dric Goffin
 * 01/10/2018 14:03
 * @see CameraLocation
 * @see Segment
 * @see CameraType
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tblCamera")
public class Camera {
    @Id
    private int cameraId;
    @OneToOne(targetEntity = CameraLocation.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private CameraLocation location;
    @OneToOne(targetEntity = Segment.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Segment segment;
    private int euroNorm;
    private CameraType cameraType;
    @ManyToMany(targetEntity = Fine.class, mappedBy = "cameras", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Fine> fines = new ArrayList<>();

    /**
     * Setter for the property cameraType
     *
     * @param cameraType is the type of camera
     * @see be.kdg.processor.camera.dom.CameraType
     */
    public void setCameraType(CameraType cameraType) {
        this.cameraType = cameraType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Camera camera = (Camera) o;

        return cameraId == camera.cameraId;
    }

    @Override
    public int hashCode() {
        return cameraId;
    }
}

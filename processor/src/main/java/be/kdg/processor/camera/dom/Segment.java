package be.kdg.processor.camera.dom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Model class that holds information about a camera linked to another camera. Only available for cameras that have the SPEED or SPEED_EMISSION CameraType. Gets stored in an H2 in-memory database in a table named 'tblCameraSegment'.
 *
 * @author Cédric Goffin
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tblCameraSegment")
public class Segment {
    @Id
    private int connectedCameraId;
    private int distance;
    private int speedLimit;
}

package be.kdg.processor.camera.dom;

import lombok.*;

import javax.persistence.*;

/**
 * @author Cédric Goffin
 * 01/10/2018 14:24
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

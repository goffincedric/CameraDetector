package be.kdg.processor.camera.dom;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Cédric Goffin
 * 01/10/2018 14:24
 */

@Getter
@Setter(AccessLevel.NONE)
@EqualsAndHashCode
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

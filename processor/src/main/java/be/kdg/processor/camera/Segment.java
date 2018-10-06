package be.kdg.processor.camera;

import lombok.*;

/**
 * @author Cédric Goffin
 * 01/10/2018 14:24
 */

@Getter
@Setter(AccessLevel.NONE)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Segment {
    private int connectedCameraId;
    private int distance;
    private int speedLimit;
}

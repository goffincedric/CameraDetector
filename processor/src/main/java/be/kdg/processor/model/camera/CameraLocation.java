package be.kdg.processor.model.camera;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * @author Cédric Goffin
 * 01/10/2018 14:04
 */

@Getter
@Setter(AccessLevel.NONE)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CameraLocation {
    @JsonProperty("lat")
    private Double latitude;
    @JsonProperty("long")
    private Double longitude;
}

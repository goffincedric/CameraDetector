package be.kdg.processor.camera.dom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;

/**
 * 
 * @author Cédric Goffin
 */

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "tblCameraLocation")
public class CameraLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @JsonProperty("lat")
    private Double latitude;
    @JsonProperty("long")
    private Double longitude;

    public CameraLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

package be.kdg.processor.camera.dom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Model class that holds information about the location of a camera. Gets stored in an H2 in-memory database in a table named 'tblCameraLocation'.
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
}

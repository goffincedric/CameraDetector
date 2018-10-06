package be.kdg.processor.camera;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter(AccessLevel.NONE)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "tblCameraMessage")
public class CameraMessage {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int messageId;
    private int cameraId;
    private String licenseplate;
    private LocalDateTime timestamp;
    private int delay = 0;

    public CameraMessage(int cameraId, String licenseplate, LocalDateTime timestamp) {
        this.cameraId = cameraId;
        this.licenseplate = licenseplate;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("Camera Message (camera_id: %d) %s %s (delay: %d)", cameraId, licenseplate, timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), delay);
    }
}


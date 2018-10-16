package be.kdg.processor.camera.dom;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tblCameraMessage")
public class CameraMessage {
    @JsonIgnore
    // TODO: Verwijder hardcoded
    @Value("${licenseplate.regex}")
    private static final String LICENSEPLATEREGEX = "^[1-8]-[A-Z]{3}-[0-9]{3}$";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int messageId;
    private int cameraId;
    private byte[] cameraImage;
    private String licenseplate;
    private LocalDateTime timestamp;
    private long delay = -1;

    public CameraMessage(int cameraId, String licenseplate, LocalDateTime timestamp) {
        this.cameraId = cameraId;
        this.setLicenseplate(licenseplate);
        this.timestamp = timestamp;
    }

    public CameraMessage(int cameraId, String licenseplate, long delay) {
        this.cameraId = cameraId;
        this.setLicenseplate(licenseplate);
        this.delay = delay;
    }

    public CameraMessage(int cameraId, String licenseplate, LocalDateTime timestamp, long delay) {
        this.cameraId = cameraId;
        this.setLicenseplate(licenseplate);
        this.timestamp = timestamp;
        this.delay = delay;
    }

    public CameraMessage(int cameraId, byte[] cameraImage, LocalDateTime timestamp, long delay) {
        this.cameraId = cameraId;
        this.cameraImage = cameraImage;
        this.timestamp = timestamp;
        this.delay = delay;
    }

    public void setLicenseplate(String licenseplate) {
        if (licenseplate != null || cameraImage == null) {
            if (!licenseplate.matches(LICENSEPLATEREGEX))
                throw new IllegalArgumentException("Invalid License plate");
            this.licenseplate = licenseplate;
        }

    }

    @Override
    public String toString() {
        return String.format("Camera Message (camera_id: %d) %s %s (delay: %d)", cameraId, licenseplate, timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), delay);
    }
}


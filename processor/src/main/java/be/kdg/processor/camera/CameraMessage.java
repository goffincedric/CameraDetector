package be.kdg.processor.camera;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter(AccessLevel.NONE)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CameraMessage {
    @JsonIgnore
    @Value("${licenseplate.regex}")
    private String licenseplateRegex = "^[1-8]-[A-Z]{3}-[0-9]{3}$";

    private int id;
    private String licenseplate;
    private byte[] cameraImage;
    private LocalDateTime timestamp;
    private long delay = 0;

    public CameraMessage(int id, String licenseplate, LocalDateTime timestamp) {
        this.id = id;
        this.setLicenseplate(licenseplate);
        this.timestamp = timestamp;
    }

    public CameraMessage(int id, String licenseplate, long delay) {
        this.id = id;
        this.setLicenseplate(licenseplate);
        this.delay = delay;
    }

    public CameraMessage(int id, String licenseplate, LocalDateTime timestamp, long delay) {
        this.id = id;
        this.setLicenseplate(licenseplate);
        this.timestamp = timestamp;
        this.delay = delay;
    }

    public CameraMessage(int id, byte[] cameraImage, LocalDateTime timestamp, long delay) {
        this.id = id;
        this.cameraImage = cameraImage;
        this.timestamp = timestamp;
        this.delay = delay;
    }

    public void setLicenseplate(String licenseplate) {
        if (!licenseplate.matches(licenseplateRegex))
            throw new IllegalArgumentException("Invalid License plate");
        this.licenseplate = licenseplate;
    }

    @Override
    public String toString() {
        return String.format("Camera Message (camera_id: %d) %s %s (delay: %d)", id, licenseplate, timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), delay);
    }
}


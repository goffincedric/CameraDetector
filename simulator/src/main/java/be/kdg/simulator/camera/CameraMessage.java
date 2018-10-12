package be.kdg.simulator.camera;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter(AccessLevel.NONE)
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CameraMessage {
    @JsonIgnore
    @Value("${licenseplate.regex}")
    private String licenseplateRegex = "^[1-8]-[A-Z]{3}-[0-9]{3}$";

    private int messageId;
    private int cameraId;
    private String licenseplate;
    private byte[] cameraImage;
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
        if (!licenseplate.matches(licenseplateRegex))
            throw new IllegalArgumentException("Invalid License plate");
        this.licenseplate = licenseplate;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public String[] toStringArray() {
        return new String[]{String.valueOf(cameraId), licenseplate, String.valueOf(delay)};
    }

    @Override
    public String toString() {
        return String.format("Camera Message (camera_id: %d) %s %s (delay: %d)", cameraId, licenseplate, timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), delay);
    }
}


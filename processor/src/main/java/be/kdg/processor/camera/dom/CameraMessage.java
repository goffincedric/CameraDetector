package be.kdg.processor.camera.dom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;


/**
 * Model class that holds information about a camera message. Gets received via a MQTT queue and processed by the Processor class.
 *
 * @author Cédric Goffin
 * @see be.kdg.processor.camera.consumers.EventConsumer
 * @see be.kdg.processor.processor.Processor
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CameraMessage {
    @JsonIgnore
    @Value("${licenseplate.regexp}")
    private static final String LICENSEPLATEREGEX = "^[1-8]-[A-Z]{3}-[0-9]{3}$";

    private int cameraId;
    private byte[] cameraImage;
    private String licenseplate;
    private LocalDateTime timestamp;
    private long delay = -1;

    /**
     * Custom constructor that gets used by the CloudALPR test. The returned CameraMessage doesn't have a license plate, but does contain an image of a license plate that can be recognised by CloudALPR.
     *
     * @param cameraId    is the camera id
     * @param cameraImage is an image of a license plate. Should get recognised by CloudALPR
     * @param timestamp   is the timestamp of when the CameraMessage got created
     * @param delay       is a custom delay given to a CameraMessage, to simulate network delay
     */
    public CameraMessage(int cameraId, byte[] cameraImage, LocalDateTime timestamp, long delay) {
        this.cameraId = cameraId;
        this.cameraImage = cameraImage;
        this.timestamp = timestamp;
        this.delay = delay;
    }

    /**
     * Setter for license plate. Checks if the supplied license plate id is valid via a regex, which can be changed in the application.properties.
     *
     * @param licenseplate a string containing a valid license plate id
     * @throws IllegalArgumentException when an invalid license plate id was given.
     */
    public void setLicenseplate(String licenseplate) throws IllegalArgumentException {
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

    /**
     * @return a string array containing several properties, which can be used to log a message when it fails to process for # times (can be configured via application.properties).
     */
    public String[] toStringArray() {
        return new String[]{String.valueOf(cameraId), (cameraImage == null) ? null : Base64.getEncoder().encodeToString(cameraImage), licenseplate, String.valueOf(timestamp), String.valueOf(delay)};
    }
}
package be.kdg.simulator.camera;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model class that holds information about a camera message. Gets sent to an MQTT queue via QueueMessenger or to console via CommandlineMessenger.
 *
 * @author C&eacute;dric Goffin
 * @see be.kdg.simulator.messenger.QueueMessenger
 * @see be.kdg.simulator.messenger.CommandlineMessenger
 * @see be.kdg.simulator.generator.FileGenerator
 * @see be.kdg.simulator.generator.RandomGenerator
 * @see be.kdg.simulator.generator.ImageGenerator
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CameraMessage {
    @JsonIgnore
    @Value("${licenseplate.regexp}")
    private String licenseplateRegex = "^[1-8]-[A-Z]{3}-[0-9]{3}$";

    private int cameraId;
    private String licenseplate;
    private byte[] cameraImage;
    private LocalDateTime timestamp;
    private long delay = -1;

    /**
     * @param cameraId     a camera id
     * @param licenseplate a string containing a valid license plate id
     * @param timestamp    a timestamp from when the message was generated
     */
    public CameraMessage(int cameraId, String licenseplate, LocalDateTime timestamp) {
        this.cameraId = cameraId;
        this.setLicenseplate(licenseplate);
        this.timestamp = timestamp;
    }

    /**
     * @param cameraId     a camera id
     * @param licenseplate a string containing a valid license plate id
     * @param delay        a delay in milliseconds to artificially create delay
     */
    public CameraMessage(int cameraId, String licenseplate, long delay) {
        this.cameraId = cameraId;
        this.setLicenseplate(licenseplate);
        this.delay = delay;
    }

    /**
     * @param cameraId     a camera id
     * @param licenseplate a string containing a valid license plate id
     * @param timestamp    a timestamp from when the message was generated
     * @param delay        a delay in milliseconds to artificially create delay
     */
    public CameraMessage(int cameraId, String licenseplate, LocalDateTime timestamp, long delay) {
        this.cameraId = cameraId;
        this.setLicenseplate(licenseplate);
        this.timestamp = timestamp;
        this.delay = delay;
    }

    /**
     * Constructor used in ImageGenerator
     *
     * @param cameraId    a camera id
     * @param cameraImage a byte array representing an image
     * @param timestamp   a timestamp from when the message was generated
     * @param delay       a delay in milliseconds to artificially create delay
     */
    public CameraMessage(int cameraId, byte[] cameraImage, LocalDateTime timestamp, long delay) {
        this.cameraId = cameraId;
        this.cameraImage = cameraImage;
        this.timestamp = timestamp;
        this.delay = delay;
    }

    /**
     * Setter for license plate
     *
     * @param licenseplate a string containing a valid license plate id
     * @throws IllegalArgumentException when an invalid license plate id was given.
     */
    public void setLicenseplate(String licenseplate) throws IllegalArgumentException {
        if (!licenseplate.matches(licenseplateRegex))
            throw new IllegalArgumentException("Invalid License plate");
        this.licenseplate = licenseplate;
    }

    /**
     * Setter for delay
     *
     * @param delay a delay in milliseconds to artificially create delay
     */
    public void setDelay(long delay) {
        this.delay = delay;
    }

    /**
     * @return a string array containing several properties, which can be used to log a message to later use it in the file generator.
     */
    public String[] toStringArray() {
        return new String[]{String.valueOf(cameraId), licenseplate, String.valueOf(delay)};
    }

    @Override
    public String toString() {
        return String.format("Camera Message (camera_id: %d) %s %s (delay: %d)", cameraId, licenseplate, timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), delay);
    }
}


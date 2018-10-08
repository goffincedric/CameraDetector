package be.kdg.processor.camera;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter(AccessLevel.NONE)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CameraMessage {
    private int id;
    private String licenseplate;
    private LocalDateTime timestamp;
    private long delay = 0;

    public CameraMessage(int id, String licenseplate, LocalDateTime timestamp) {
        this.id = id;
        this.licenseplate = licenseplate;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("Camera Message (camera_id: %d) %s %s (delay: %d)", id, licenseplate, timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), delay);
    }
}


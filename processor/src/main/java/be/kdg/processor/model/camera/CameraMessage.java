package be.kdg.processor.model.camera;
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
    private String licenceplate;

    private LocalDateTime timestamp;
    private int delay = 0;

    public CameraMessage(int id, String licenceplate, LocalDateTime timestamp) {
        this.id = id;
        this.licenceplate = licenceplate;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("Camera Message (camera_id: %d) %s %s (delay: %d)", id, licenceplate, timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), delay);
    }
}


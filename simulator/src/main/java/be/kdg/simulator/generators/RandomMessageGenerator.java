package be.kdg.simulator.generators;

import be.kdg.simulator.model.CameraMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

public class RandomMessageGenerator implements MessageGenerator {

    @Override
    public CameraMessage generate() {
        return new CameraMessage(1, "1-ABC-123", LocalDateTime.now());
    }
}

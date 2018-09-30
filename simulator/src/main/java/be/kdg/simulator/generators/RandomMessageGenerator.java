package be.kdg.simulator.generators;

import be.kdg.simulator.model.CameraMessage;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class RandomMessageGenerator implements MessageGenerator {
    private static final Logger LOGGER = Logger.getLogger(RandomMessageGenerator.class.getName());

    @Override
    public CameraMessage generate() {
        Random random = new Random();
        CameraMessage message = new CameraMessage(
                random.nextInt(1000),
                String.format("%s-%S-%s", RandomStringUtils.random(1, "123456789"), RandomStringUtils.random(3, true, false), RandomStringUtils.random(3, false, true)),
                LocalDateTime.now()
        );
        LOGGER.info("Randomly generated: " + message);
        return message;
    }
}

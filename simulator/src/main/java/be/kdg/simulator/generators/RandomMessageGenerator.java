package be.kdg.simulator.generators;

import be.kdg.simulator.model.CameraMessage;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.logging.Logger;

public class RandomMessageGenerator implements MessageGenerator {
    private static final Logger LOGGER = Logger.getLogger(RandomMessageGenerator.class.getName());

    @Override
    public CameraMessage generate() {
        Random random = new Random();
        CameraMessage message = new CameraMessage(
                random.nextInt(5)+1,
                String.format("%s-%S-%s", RandomStringUtils.random(1, "12345678"), RandomStringUtils.random(3, true, false), RandomStringUtils.random(3, false, true)),
                LocalDateTime.now(),
                0
        );
        LOGGER.info("Randomly generated: " + message);
        return message;
    }
}

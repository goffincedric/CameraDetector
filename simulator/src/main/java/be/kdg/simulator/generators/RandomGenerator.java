package be.kdg.simulator.generators;

import be.kdg.simulator.camera.CameraMessage;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.logging.Logger;

public class RandomGenerator implements MessageGenerator {
    private static final Logger LOGGER = Logger.getLogger(RandomGenerator.class.getName());

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

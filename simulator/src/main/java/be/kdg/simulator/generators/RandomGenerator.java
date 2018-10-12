package be.kdg.simulator.generators;

import be.kdg.simulator.camera.CameraMessage;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

public class RandomGenerator implements MessageGenerator {
    private static final Logger LOGGER = Logger.getLogger(RandomGenerator.class.getName());
    private Map<String, LocalDateTime> lastTimestamps;

    public RandomGenerator() {
        lastTimestamps = new HashMap<>();
    }

    @Override
    public CameraMessage generate() {
        Random random = new Random();

        // Check first run & adjust lastTimestamp
        long delay = 0;
        String threadId = Thread.currentThread().getName();
        if (lastTimestamps.containsKey(threadId)) {
            LocalDateTime lastTimestamp = lastTimestamps.get(threadId);
            delay = ChronoUnit.MILLIS.between(lastTimestamp, LocalDateTime.now());
        }
        lastTimestamps.put(threadId, LocalDateTime.now());

        CameraMessage message = new CameraMessage(
                random.nextInt(5)+1,
                String.format("%s-%S-%s", RandomStringUtils.random(1, "12345678"), RandomStringUtils.random(3, true, false), RandomStringUtils.random(3, false, true)),
                LocalDateTime.now(),
                delay
        );
        LOGGER.info("Randomly generated: " + message);
        return message;
    }
}

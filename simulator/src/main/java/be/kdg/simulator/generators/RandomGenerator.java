package be.kdg.simulator.generators;

import be.kdg.simulator.camera.CameraMessage;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Generator class that randomly generates CameraMessages.
 *
 * @author Cédric Goffin
 * @see MessageGenerator
 * @see CameraMessage
 */
public class RandomGenerator implements MessageGenerator {
    private static final Logger LOGGER = Logger.getLogger(RandomGenerator.class.getName());
    private Map<String, LocalDateTime> lastTimestamps;

    /**
     * Default constructor for RandomGenerator.
     */
    public RandomGenerator() {
        lastTimestamps = new HashMap<>();
    }

    /**
     * Generates CameraMessages with randomly generated license plate id and camera id.
     *
     * @return a randomly generated Cameramessage.
     */
    @Override
    public Optional<CameraMessage> generate() {
        Random random = new Random();

        // Check first run & adjust lastTimestamp
        long delay = 0;
        String threadId = Thread.currentThread().getName();
        if (lastTimestamps.containsKey(threadId)) {
            LocalDateTime lastTimestamp = lastTimestamps.get(threadId);
            delay = ChronoUnit.MILLIS.between(lastTimestamp, LocalDateTime.now());
        }
        lastTimestamps.put(threadId, LocalDateTime.now());

        Optional<CameraMessage> message = Optional.of(new CameraMessage(
                random.nextInt(5) + 1,
                String.format("%s-%S-%s", RandomStringUtils.random(1, "12345678"), RandomStringUtils.random(3, true, false), RandomStringUtils.random(3, false, true)),
                LocalDateTime.now(),
                delay
        ));
        LOGGER.info("Randomly generated: " + message.get());
        return message;
    }
}

package be.kdg.simulator.generators;

import be.kdg.simulator.camera.CameraMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Logger;

/**
 * Generator class that generates CameraMessages without license plate id, but with an image stored as a byte array.
 *
 * @author Cédric Goffin
 * @see MessageGenerator
 * @see CameraMessage
 */
public class ImageGenerator implements MessageGenerator {
    private static final Logger LOGGER = Logger.getLogger(ImageGenerator.class.getName());
    private LocalDateTime lastTimestamp = null;

    private File[] images;

    /**
     * Constructor for ImageGenerator.
     *
     * @param imagesPath a string containing a url to a directory with images (jpg/png) of license plates
     */
    public ImageGenerator(String imagesPath) {
        // Check if filesPath exists
        File dir = new File(imagesPath);
        if (!dir.isDirectory())
            throw new IllegalArgumentException("Directory with path '" + imagesPath + "' does not exist!");
        if (dir.listFiles() == null)
            throw new IllegalArgumentException("Directory with path '" + imagesPath + "' is empty!");
        else {
            // Get all images from dir
            images = Arrays.stream(Objects.requireNonNull(dir.listFiles())).filter(f -> f.getName().toLowerCase().endsWith(".jpg") || f.getName().toLowerCase().endsWith(".png")).toArray(File[]::new);
            if (images.length == 0)
                throw new IllegalArgumentException("No images (jpg/png) were found in directory '" + imagesPath + "'");
        }
    }

    /**
     * Generates CameraMessages with one randomly chosen image from list with images.
     *
     * @return a Cameramessage with an image as byte array
     */
    public Optional<CameraMessage> generate() {
        Random random = new Random();
        Optional<CameraMessage> message = Optional.empty();

        // Choose random image
        File image = images[random.nextInt(images.length)];

        // Read image file to byte array
        try {
            Path path = Paths.get(image.getPath());
            byte[] data = Files.readAllBytes(path);

            // Check first run & adjust lastTimestamp
            long delay = 0;
            if (lastTimestamp != null) {
                delay = ChronoUnit.MILLIS.between(lastTimestamp, LocalDateTime.now());
            }
            lastTimestamp = LocalDateTime.now();

            // Encode file bytes to base64 and put in new message
            message = Optional.of(new CameraMessage(
                    random.nextInt(5) + 1,
                    Base64.getEncoder().encode(data),
                    LocalDateTime.now(),
                    delay
            ));
            LOGGER.info("Randomly generated: " + message.get());
        } catch (IOException e) {
            LOGGER.severe("Something went wrong while trying to read image at path '" + image.getPath() + "'");
        }

        return message;
    }
}

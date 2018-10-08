package be.kdg.simulator.generators;

import be.kdg.simulator.camera.CameraMessage;
import org.apache.commons.lang3.RandomStringUtils;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Logger;

/**
 * @author Cédric Goffin
 * 08/10/2018 13:56
 */
public class ImageGenerator implements MessageGenerator {
    private static final Logger LOGGER = Logger.getLogger(ImageGenerator.class.getName());

    private File[] images;

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

    public CameraMessage generate() {
        Random random = new Random();
        CameraMessage message = null;

        // Choose random image
        File image = images[random.nextInt(images.length)];

        // Read image file to byte array
        try {
            Path path = Paths.get(image.getPath());
            byte[] data = Files.readAllBytes(path);

            // Encode file bytes to base64 and put in new message
            message = new CameraMessage(
                    random.nextInt(5) + 1,
                    Base64.getEncoder().encode(data),
                    LocalDateTime.now(),
                    0
            );
            LOGGER.info("Randomly generated: " + message);
        } catch (IOException e) {
            LOGGER.severe("Something went wrong while trying to read image at path '" + image.getPath() + "'");
        }

        return message;
    }
}

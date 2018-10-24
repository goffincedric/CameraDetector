package be.kdg.simulator.generators;

import be.kdg.simulator.camera.CameraMessage;
import be.kdg.simulator.utils.CSVUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Generator class that generates CameraMessages from a CSV file or directory containing multiple CSV files (configurable via application.properties).
 *
 * @author C&eacute;dric Goffin
 * @see MessageGenerator
 * @see CameraMessage
 */
public class FileGenerator implements MessageGenerator {
    private static final Logger LOGGER = Logger.getLogger(FileGenerator.class.getName());

    private List<CameraMessage> messages;

    /**
     * Constructor for FileGenerator.
     *
     * @param filePath a string containing a url to a directory with deserializable CSV files or one particular deserializable CSV file
     */
    public FileGenerator(String filePath) {
        messages = new ArrayList<>();

        File path = new File(filePath);
        if (Files.isRegularFile(path.toPath())) {
            messages.addAll(CSVUtils.getMessagesFromFile(filePath));
        } else if (path.isDirectory()) {
            LOGGER.info("Given path is a directory (" + filePath + "). Will attempt to read all csv files in this dir.");
            try {
                messages.addAll(
                        Files.walk(path.toPath(), 1)
                                .map(Path::toString)
                                .filter(p -> FilenameUtils.getExtension(p).equalsIgnoreCase("csv"))
                                .map(CSVUtils::getMessagesFromFile)
                                .flatMap(List::stream)
                                .collect(Collectors.toList())
                );
            } catch (IOException e) {
                LOGGER.severe("Problem occurred while reading the messages from CSV files!");
            }
        }
    }

    /**
     * Generates a CameraMessage by removing one from the list.
     *
     * @return a Cameramessage that was deserialized from a CSV file
     */
    @Override
    public Optional<CameraMessage> generate() {
        if (messages.isEmpty()) {
            System.exit(0);
        }
        CameraMessage message = messages.remove(0);
        message.setTimestamp(LocalDateTime.now());
        LOGGER.info("Generated from file: " + message);

        // Artificially create delay
        try {
            Thread.sleep(message.getDelay());
        } catch (InterruptedException e) {
            LOGGER.severe("Could not pause thread in " + this.getClass().getSimpleName() + "!");
        }

        return Optional.of(message);
    }
}

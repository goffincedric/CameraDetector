package be.kdg.simulator.generators;

import be.kdg.simulator.camera.CameraMessage;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FileGenerator implements MessageGenerator {
    private static final Logger LOGGER = Logger.getLogger(FileGenerator.class.getName());

    @Getter
    private List<CameraMessage> messages;

    public FileGenerator(String filePath) {
        messages = new ArrayList<>();

        File path = new File(filePath);
        if (Files.isRegularFile(path.toPath())) {
            messages.addAll(getMessagesFromFile(filePath));
        } else if (path.isDirectory()) {
            LOGGER.info("Given path is a directory (" + filePath + "). Will attempt to read all csv files in this dir.");
            try {
                messages.addAll(
                        Files.walk(path.toPath(), 1)
                                .map(Path::toString)
                                .filter(p -> FilenameUtils.getExtension(p).toLowerCase().equals("csv"))
                                .map(this::getMessagesFromFile)
                                .flatMap(List::stream)
                                .collect(Collectors.toList())
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<CameraMessage> getMessagesFromFile(String filePath) {
        List<CameraMessage> messageBuffer = new ArrayList<>();

        try {
            // Create an object of file reader class with path to CSV file as a parameter.
            FileReader filereader = new FileReader(filePath);

            // Create csvReader object and skip first line (Header line)
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            List<String[]> allData = csvReader.readAll();

            // Create message list from CSV lines
            messageBuffer.addAll(
                    allData.stream()
                            .map(row -> {
                                try {
                                    return new CameraMessage(
                                            Integer.parseInt(row[0]),
                                            row[1],
                                            LocalDateTime.now(),
                                            Integer.parseInt(row[2]));
                                } catch (IllegalArgumentException iae) {
                                    LOGGER.severe(iae.getMessage());
                                }
                                return null;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
            );
        } catch (FileNotFoundException fnfe) {
            LOGGER.severe("File with path '" + filePath + "' does not exist!");
            fnfe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return messageBuffer;
    }

    @Override
    public CameraMessage generate() {
        if (messages.isEmpty()) System.exit(0);
        CameraMessage message = messages.remove(0);

        // Artificially create delay
        try {
            Thread.sleep(message.getDelay());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOGGER.info("Generated from file: " + message);
        return message;
    }
}

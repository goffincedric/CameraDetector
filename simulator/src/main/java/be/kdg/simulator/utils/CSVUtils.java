package be.kdg.simulator.utils;

import be.kdg.simulator.camera.CameraMessage;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import lombok.experimental.UtilityClass;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Utility class for CSV file logging and CSV deserialization.
 *
 * @author Cédric Goffin
 */
@UtilityClass
public class CSVUtils {
    private final Logger LOGGER = Logger.getLogger(CSVUtils.class.getName());
    private String fileTemplate = "log_" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_"));

    private LocalDateTime lastTimestamp = null;
    private String fileName = null;

    /**
     * Reads a CSV file and puts the deserialized CameraMessages in a list.
     *
     * @param filePath a string containing a url to one particular deserializable CSV file
     * @return a list of CameraMessages that where deserialized from the CSV file
     */
    public List<CameraMessage> getMessagesFromFile(String filePath) {
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

    /**
     * Logs failed messages to a CSV file.
     *
     * @param message     is a CameraMessage that failed to process # times (configurable via application.properties)
     * @param logger_path is the path where the CSV file should be written (configurable via application.properties)
     */
    public void writeMessage(CameraMessage message, String logger_path) {
        if (lastTimestamp == null) {
            if (logger_path == null) throw new IllegalArgumentException("No path to logfile was given!");

            // Check if dir exists
            File directory = new File(logger_path);
            if (!directory.exists()) directory.mkdirs();


            int id = 0;
            File filepath = new File(logger_path + fileTemplate + id + ".csv");
            while (filepath.exists()) {
                id++;
                filepath = new File(logger_path + fileTemplate + id + ".csv");
            }
            fileName = logger_path + fileTemplate + id + ".csv";
        }

        try (
                FileWriter writer = new FileWriter(fileName, true);

                CSVWriter csvWriter = new CSVWriter(writer,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END)
        ) {
            // Check first run
            if (lastTimestamp == null) {
                // Write header
                String[] fieldNames = {"id", "licenseplate", "delay"};
                csvWriter.writeNext(fieldNames);

                // Set lastTimestamp
                lastTimestamp = message.getTimestamp();
            }

            // Calculate delay between previous and this message
            if (message.getDelay() == 0) {
                long delay = ChronoUnit.MILLIS.between(lastTimestamp, message.getTimestamp());
                message.setDelay(delay);
            }
            lastTimestamp = message.getTimestamp();

            csvWriter.writeNext(message.toStringArray());
            LOGGER.info("Logged message: " + message);
        } catch (IOException e) {
            LOGGER.severe("Target CSV file could not be found!");
        }
    }
}

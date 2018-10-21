package be.kdg.processor.utils;

import be.kdg.processor.camera.dom.CameraMessage;
import com.opencsv.CSVWriter;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

/**
 * @author Cédric Goffin
 * 08/10/2018 11:13
 */
@UtilityClass
public class CSVUtils {
    private final Logger LOGGER = Logger.getLogger(CSVUtils.class.getName());
    private String fileTemplate = "failed_log_" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_"));

    private String fileName = null;
    private boolean firstRun = true;

    public void writeMessage(CameraMessage message, String logger_path) {
        if (logger_path == null) throw new IllegalArgumentException("No path to logfile was given!");
        File directory = new File(logger_path);

        // Check if dir exists
        if (firstRun) {
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
            if (firstRun) {
                // Write header
                String[] fieldNames = {"id", "cameraImage", "licenseplate", "timestamp", "delay"};
                csvWriter.writeNext(fieldNames);
                firstRun = false;
            }

            csvWriter.writeNext(message.toStringArray());
            LOGGER.info("Logged message: " + message);
        } catch (IOException e) {
            LOGGER.severe("Target CSV file could not be found!");
        }
    }
}

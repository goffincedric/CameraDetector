package be.kdg.processor.utils;

import be.kdg.processor.camera.dom.CameraMessage;
import com.opencsv.CSVWriter;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * Utility class for CSV file logging.
 *
 * @author C&eacute;dric Goffin
 */
@UtilityClass
public class CSVUtils {
    private final Logger LOGGER = Logger.getLogger(CSVUtils.class.getName());
    private String fileTemplate = "failed_log_" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_"));

    private String fileName = null;
    private boolean firstRun = true;

    /**
     * Logs failed messages to a CSV file.
     *
     * @param message     is a CameraMessage that failed to process # times (configurable via application.properties)
     * @param logger_path is the path where the CSV file should be written (configurable via application.properties)
     */
    public String writeMessage(CameraMessage message, String logger_path) {
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
        } catch (IOException e) {
            LOGGER.severe("Target CSV file could not be found!");
        }

        return fileName;
    }
}

package be.kdg.simulator.utils;

import be.kdg.simulator.camera.CameraMessage;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Cédric Goffin
 * 08/10/2018 11:13
 */
public class CSVUtils {
    private static final Logger LOGGER = Logger.getLogger(CSVUtils.class.getName());
    private static String fileTemplate = "log_" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_"));

    private static LocalDateTime lastTimestamp = null;
    private static String fileName = null;

    public static void writeMessage(CameraMessage message, String logger_path) {
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

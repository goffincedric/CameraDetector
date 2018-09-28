package be.kdg.simulator.generators;

import be.kdg.simulator.model.CameraMessage;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.Getter;

import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileGenerator implements MessageGenerator {

    @Getter
    private List<CameraMessage> messages;

    public FileGenerator(String file_path) {
        messages = new ArrayList<>();

        try {
            // Create an object of file reader class with path to CSV file as a parameter.
            FileReader filereader = new FileReader(file_path);

            // Create csvReader object and skip first line (Header line)
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            List<String[]> allData = csvReader.readAll();

            // Create message list from CSV lines
            for (String[] row : allData) {
                CameraMessage message =new CameraMessage(
                        Integer.parseInt(row[0]),
                        row[1], LocalDateTime.parse(row[2], DateTimeFormatter.ofPattern("yyyy-M-d H:mm:ss")),
                        Integer.parseInt(row[3]));
                messages.add(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public CameraMessage generate() {
        if (messages.isEmpty())
            System.exit(0);
        return messages.remove(0);
    }
}

package be.kdg.simulator.generators;

import be.kdg.simulator.model.CameraMessage;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FileGenerator implements MessageGenerator {
    private List<CameraMessage> messages;

    public FileGenerator(String csvPath) {
        messages = new ArrayList<>();

        try {
            // Create an object of file reader class with path to CSV file as a parameter.
            FileReader filereader = new FileReader(csvPath);

            // Create csvReader object and skip first line (Header line)
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            List<String[]> allData = csvReader.readAll();

            // Create message list from CSV lines
            for (String[] row : allData) {
                CameraMessage message =new CameraMessage(
                        Integer.parseInt(row[0]),
                        row[1], LocalDateTime.parse(row[2], DateTimeFormatter.ofPattern("yyyy-M-d H:mm:ss")));
                messages.add(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public CameraMessage generate() {
        return messages.get(new Random().nextInt(messages.size()));
    }
}

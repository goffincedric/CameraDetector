package be.kdg.simulator.generators;

import be.kdg.simulator.model.CameraMessage;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FileGenerator implements MessageGenerator {
    private String csvPath;

    public FileGenerator(String csvPath) {
        this.csvPath = csvPath;
    }

    @Override
    public CameraMessage generate() {
        return null;
    }

    @Override
    public List<CameraMessage> generateList() {
        List<CameraMessage> messages = new ArrayList<>();

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
                        row[1], LocalDateTime.parse(row[2]));
                messages.add(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return messages;
    }
}

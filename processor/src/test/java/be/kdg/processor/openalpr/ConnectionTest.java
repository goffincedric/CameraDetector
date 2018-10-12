package be.kdg.processor.openalpr;

import be.kdg.processor.camera.dom.CameraMessage;
import be.kdg.processor.processor.services.CloudALPRService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * @author Cédric Goffin
 * 08/10/2018 13:27
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ConnectionTest {

    @Autowired
    private CloudALPRService cloudALPRService;

    @Test
    public void testConnection() throws IOException {

        // Read image file to byte array
        Path path = Paths.get("src/test/resources/images/1.jpg");
        byte[] image = Files.readAllBytes(path);

        // Encode file bytes to base64 and put in new message
        CameraMessage message = new CameraMessage(
                1,
                Base64.getEncoder().encode(image),
                LocalDateTime.now(),
                0
        );

        String licenseplate = cloudALPRService.getLicenseplate(message.getCameraImage());
        Assert.assertEquals("1-EAF-955", licenseplate);
    }
}

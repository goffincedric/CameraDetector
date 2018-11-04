package be.kdg.processor.openalpr;

import be.kdg.processor.camera.dom.CameraMessage;
import be.kdg.processor.licenseplate.services.CloudALPRService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * @author C&eacute;dric Goffin
 * 08/10/2018 13:27
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CloudALPRServiceTests {

    @Autowired
    private CloudALPRService cloudALPRService;

    @Test
    public void testConnection() throws Exception {
        // Read image file to byte array
        ClassLoader classLoader = getClass().getClassLoader();
        Path path = new File(classLoader.getResource("images/1.jpg").toURI().getPath()).toPath();
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

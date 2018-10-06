package be.kdg.processor;

import be.kdg.processor.licenseplate.Licenseplate;
import be.kdg.processor.services.InformationService;
import be.kdg.processor.camera.Camera;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;
import java.util.logging.Logger;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProcessorApplicationTests {
    private static final Logger LOGGER = Logger.getLogger(ProcessorApplicationTests.class.getName());

    @Autowired
    InformationService informationService;

    @Test
    public void testCameraService() {
        int cameraId = new Random().nextInt(5)+1;
        Camera camera = informationService.getCamera(cameraId);
        Assert.assertNotNull(camera);
    }
    
    @Test
    public void testLicenseplateService() {
        String licenseplateId = String.format("%s-%S-%s", RandomStringUtils.random(1, "12345678"), RandomStringUtils.random(3, true, false), RandomStringUtils.random(3, false, true));
        Licenseplate licenseplate = informationService.getLicensePlate(licenseplateId);
        Assert.assertNotNull(licenseplate);
    }

}

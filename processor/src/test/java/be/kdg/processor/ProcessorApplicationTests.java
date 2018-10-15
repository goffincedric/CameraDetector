package be.kdg.processor;

import be.kdg.processor.camera.services.CameraServiceAdapter;
import be.kdg.processor.licenseplate.dom.Licenseplate;
import be.kdg.processor.camera.dom.Camera;
import be.kdg.processor.licenseplate.services.LicenseplateServiceAdapter;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProcessorApplicationTests {
    private static final Logger LOGGER = Logger.getLogger(ProcessorApplicationTests.class.getName());

    @Autowired
    private CameraServiceAdapter cameraServiceAdapter;
    @Autowired
    private LicenseplateServiceAdapter licenseplateServiceAdapter;

    @Test
    public void testCameraService() {
        int cameraId = new Random().nextInt(5)+1;
        Optional<Camera> optionalCamera = cameraServiceAdapter.getCamera(cameraId);
        Assert.assertTrue(optionalCamera.isPresent());
        optionalCamera.ifPresent(camera -> Assert.assertEquals(camera.getCameraId(), cameraId));
    }
    
    @Test
    public void testLicenseplateService() {
        String licenseplateId = String.format("%s-%S-%s", RandomStringUtils.random(1, "12345678"), RandomStringUtils.random(3, true, false), RandomStringUtils.random(3, false, true));
        Optional<Licenseplate> optionalLicenseplate = licenseplateServiceAdapter.getLicensePlate(licenseplateId);
        Assert.assertTrue(optionalLicenseplate.isPresent());
        optionalLicenseplate.ifPresent(licenseplate -> Assert.assertTrue(licenseplate.getPlateId().equalsIgnoreCase(licenseplateId)));
    }

}

package be.kdg.processor.camera.services;

import be.kdg.processor.camera.dom.Camera;
import be.kdg.sa.services.CameraNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

/**
 * @author C&eacute;dric Goffin
 * 16/10/2018 16:14
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CameraServiceAdapterTests {

    @Autowired
    private CameraServiceAdapter cameraServiceAdapter;

    @Test
    public void testCameraService() throws CameraNotFoundException {
        int cameraId = new Random().nextInt(5) + 1;
        System.out.println(cameraId);
        Optional<Camera> optionalCamera = cameraServiceAdapter.getCamera(cameraId);
        System.out.println(optionalCamera);
        Assert.assertTrue(optionalCamera.isPresent());
        optionalCamera.ifPresent(camera -> {
            switch (camera.getCameraType()) {
                case SPEED:
                    Assert.assertEquals(0, camera.getEuroNorm());
                    break;
                case EMISSION:
                    Assert.assertNull(camera.getSegment());
                case SPEED_EMISSION:
                    Assert.assertTrue(camera.getEuroNorm() > 0);
                    break;
            }
            Assert.assertEquals(camera.getCameraId(), cameraId);
        });
    }
}

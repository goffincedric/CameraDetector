package be.kdg.processor.camera;

import be.kdg.processor.camera.dom.Camera;
import be.kdg.processor.camera.services.CameraServiceAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.Random;

/**
 * @author C&eacute;dric Goffin
 * 16/10/2018 16:14
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CameraServiceAdapterTests {

    @Autowired
    private CameraServiceAdapter cameraServiceAdapter;

    @Test
    public void testCameraService() {
        int cameraId = new Random().nextInt(5)+1;
        Optional<Camera> optionalCamera = cameraServiceAdapter.getCamera(cameraId);
        Assert.assertTrue(optionalCamera.isPresent());
        optionalCamera.ifPresent(camera -> Assert.assertEquals(camera.getCameraId(), cameraId));
    }
}

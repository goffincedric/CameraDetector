package be.kdg.processor;

import be.kdg.processor.misc.JSONUtils;
import be.kdg.processor.model.camera.Camera;
import be.kdg.sa.services.CameraNotFoundException;
import be.kdg.sa.services.CameraServiceProxy;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProcessorApplicationTests {
    private static final Logger LOGGER = Logger.getLogger(ProcessorApplicationTests.class.getName());

    @Autowired
    CameraServiceProxy cameraServiceProxy;

    @Test
    public void testCameraService() {
        int cameraId = new Random().nextInt(5)+1;
        System.out.println(cameraId);
        Camera camera = null;
        try {
            camera = JSONUtils.convertJSONToObject(cameraServiceProxy.get(cameraId), Camera.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CameraNotFoundException cnfe) {
            LOGGER.severe("Camera with cameraId " + cameraId + " not found!");
            cnfe.printStackTrace();
        } finally {
            Assert.assertNotNull(camera);
        }
    }

}

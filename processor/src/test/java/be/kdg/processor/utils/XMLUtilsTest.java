package be.kdg.processor.utils;

import be.kdg.processor.camera.consumers.EventConsumer;
import be.kdg.processor.camera.dom.CameraMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static junit.framework.TestCase.fail;

/**
 * @author Cédric Goffin
 * 03/11/2018 15:23
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class XMLUtilsTest {

    @Test
    public void receiveMessage() throws IOException {
        CameraMessage cameraMessage = new CameraMessage(1, null, "1-ABC-123", LocalDateTime.now(), 0);
        Optional<String> optionalXml = XMLUtils.convertObjectToXML(cameraMessage);
        if (optionalXml.isPresent()) {
            Optional<CameraMessage> optionalCameraMessage = XMLUtils.convertXMLToObject(optionalXml.get(), CameraMessage.class);
            Assert.assertTrue(optionalCameraMessage.isPresent());
            CameraMessage deserializedCameraMessage = optionalCameraMessage.get();
            Assert.assertEquals(cameraMessage, deserializedCameraMessage);
        } else {
            fail();
        }
    }
}
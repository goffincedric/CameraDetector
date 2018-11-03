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

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Cédric Goffin
 * 03/11/2018 15:23
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class XMLUtilsTest {
    @Autowired
    private EventConsumer eventConsumer;

    @Test
    public void receiveMessage() throws JsonProcessingException {
        CameraMessage cameraMessage = new CameraMessage(1, null, "1-ABC-123", LocalDateTime.now(), 0);
        XmlMapper mapper = new XmlMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String xml = mapper.writeValueAsString(cameraMessage);

        Optional<CameraMessage> optionalCameraMessage = XMLUtils.convertXMLToObject(xml, CameraMessage.class);
        Assert.assertTrue(optionalCameraMessage.isPresent());
        CameraMessage deserializedCameraMessage = optionalCameraMessage.get();
        Assert.assertEquals(cameraMessage, deserializedCameraMessage);
    }
}
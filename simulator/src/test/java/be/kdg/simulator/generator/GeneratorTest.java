package be.kdg.simulator.generator;

import be.kdg.simulator.camera.CameraMessage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

/**
 * @author Cédric Goffin
 * 03/11/2018 16:01
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GeneratorTest {


    @Autowired
    private MessageGenerator messageGenerator;

    @Value("${licenseplate.regexp}")
    private String licenseplaceRegex;

    @Test
    public void testMessageGenerator() {
        Optional<CameraMessage> optionalMessage = messageGenerator.generate();
        Assert.assertTrue(optionalMessage.isPresent());
        Assert.assertTrue(optionalMessage.get().getLicenseplate().matches(licenseplaceRegex));
    }
}

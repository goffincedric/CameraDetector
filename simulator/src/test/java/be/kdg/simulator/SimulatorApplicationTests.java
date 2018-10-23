package be.kdg.simulator;

import be.kdg.simulator.generators.MessageGenerator;
import be.kdg.simulator.camera.CameraMessage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SimulatorApplicationTests {

    @Autowired
    private MessageGenerator messageGenerator;

    @Value("${licenseplate.regexp}")
    private String licenseplaceRegex;

    @Test
    @ConditionalOnProperty(value = "generator", havingValue = "random")
    public void testMessageGenerator() {
        Optional<CameraMessage> optionalMessage = messageGenerator.generate();
        Assert.assertTrue(optionalMessage.isPresent());
        Assert.assertTrue(optionalMessage.get().getLicenseplate().matches(licenseplaceRegex));
    }
}

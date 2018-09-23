package be.kdg.simulator;

import be.kdg.simulator.generators.MessageGenerator;
import be.kdg.simulator.model.CameraMessage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SimulatorApplicationTests {

    @Autowired
    private MessageGenerator messageGenerator;

    @Test
    @ConditionalOnProperty(value = "generator", havingValue = "random")
    public void testMessageGenerator() {
        CameraMessage cameraMessage = messageGenerator.generate();
        System.out.println(cameraMessage);
        Assert.assertTrue(cameraMessage.getLicenceplate().matches("^[1-9]-[A-Z]{3}-[0-9]{3}$"));
    }

}

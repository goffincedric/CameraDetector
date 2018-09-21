package be.kdg.simulator;

import be.kdg.simulator.generators.MessageGenerator;
import be.kdg.simulator.model.CameraMessage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SimulatorApplicationTests {

    @Autowired
    private MessageGenerator messageGenerator;

    @Test
    public void testMessageGenerator() {
        CameraMessage cameraMessage = messageGenerator.generate();
        assertThat(cameraMessage.getLicenceplate().equalsIgnoreCase("1-ABC-123"));
        Assert.assertTrue(cameraMessage.getLicenceplate().equalsIgnoreCase("1-ABC-123"));
    }

    @Test
    public void testMessageGeneratorList() {
        List<CameraMessage> cameraMessage = messageGenerator.generateList();
        cameraMessage.forEach(System.out::println);
    }

}

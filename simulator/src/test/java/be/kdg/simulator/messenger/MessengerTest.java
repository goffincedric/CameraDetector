package be.kdg.simulator.messenger;

import be.kdg.simulator.camera.CameraMessage;
import be.kdg.simulator.generator.MessageGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessengerTest {
    @Autowired
    private Messenger messenger;
    @Autowired
    private MessageGenerator generator;

    @Test
    public void sendMessage() {
        Optional<CameraMessage> optionalMessage = generator.generate();
        Assert.assertTrue(optionalMessage.isPresent());
        messenger.sendMessage(optionalMessage.get());
    }
}
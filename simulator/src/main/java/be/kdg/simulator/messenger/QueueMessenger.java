package be.kdg.simulator.messenger;

import be.kdg.simulator.generators.MessageGenerator;
import be.kdg.simulator.model.CameraMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component

public class QueueMessenger implements Messenger {

    @Override
    public void sendMessage() {

    }
}
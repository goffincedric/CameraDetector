package be.kdg.simulator.messenger;

import be.kdg.simulator.model.CameraMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@ConditionalOnProperty(name="messenger",havingValue = "cli")
public class CommandlineMessenger implements Messenger {
    private static final Logger LOGGER = Logger.getLogger(CommandlineMessenger.class.getName());

    @Override
    public void sendMessage(CameraMessage message) {
        System.out.println(message);
    }
}

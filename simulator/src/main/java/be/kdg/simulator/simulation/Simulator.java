package be.kdg.simulator.simulation;

import be.kdg.simulator.generators.MessageGenerator;
import be.kdg.simulator.messenger.Messenger;
import be.kdg.simulator.camera.CameraMessage;
import be.kdg.simulator.utils.CSVUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * @author Cédric Goffin
 * 21/09/2018 13:45
 */

@Component
public class Simulator {
    private final Messenger messenger;
    private final MessageGenerator messageGenerator;

    @Value("${messenger.log.enabled}")
    private boolean logMessages;
    @Value("${messenger.log.path}")
    private String logger_path;

    @Autowired
    public Simulator(Messenger messenger, MessageGenerator messageGenerator) {
        this.messenger = messenger;
        this.messageGenerator = messageGenerator;
    }

    public void sendMessage() {
        // Generate message
        CameraMessage message = messageGenerator.generate();

        // Artificially create delay
        try {
            Thread.sleep(message.getDelay());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Send message
        messenger.sendMessage(message);

        // Log message to file
        if (logMessages) CSVUtils.writeMessage(message, logger_path);
    }
}

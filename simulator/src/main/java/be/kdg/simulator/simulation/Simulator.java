package be.kdg.simulator.simulation;

import be.kdg.simulator.camera.CameraMessage;
import be.kdg.simulator.generator.MessageGenerator;
import be.kdg.simulator.messenger.Messenger;
import be.kdg.simulator.utils.CSVUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Class that holds the configured messenger and generator to simulate a camera network.
 *
 * @author C&eacute;dric Goffin
 */
@Component
public class Simulator {
    private static final Logger LOGGER = Logger.getLogger(Simulator.class.getName());
    private final Messenger messenger;
    private final MessageGenerator messageGenerator;

    @Value("${messenger.log.enabled}")
    private boolean logMessages;
    @Value("${messenger.log.path}")
    private String logger_path;

    /**
     * Constructor for Simulator.
     *
     * @param messenger        a messenger that will send out CameraMessages
     * @param messageGenerator a generator that wil generate CameraMessages
     */
    @Autowired
    public Simulator(Messenger messenger, MessageGenerator messageGenerator) {
        this.messenger = messenger;
        this.messageGenerator = messageGenerator;
    }

    /**
     * Method that will send the generated message and eventually log it to a CSV file (configurable via application.properties).
     */
    public void sendMessage() {
        // Generate message
        Optional<CameraMessage> optionalMessage = messageGenerator.generate();

        optionalMessage.ifPresent(message -> {
            // Send message
            try {
                messenger.sendMessage(message);
            } catch (AmqpException ae) {
                LOGGER.severe("Error ocurred when sending message: " + ae.getMessage());
            }

            // Log message to file
            if (logMessages) CSVUtils.writeMessage(message, logger_path);
        });
    }
}

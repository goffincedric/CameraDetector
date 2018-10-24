package be.kdg.simulator.messenger;

import be.kdg.simulator.camera.CameraMessage;
import be.kdg.simulator.utils.XMLUtils;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Messenger class that send CameraMessages to an MQTT queue (configurable via application.properties)
 *
 * @author C&eacute;dric Goffin
 * @see Messenger
 * @see CameraMessage
 * @see QueueConfig
 */
@Component
@ConditionalOnProperty(name = "messenger", havingValue = "queue")
public class QueueMessenger implements Messenger {
    private final RabbitTemplate rabbitTemplate;
    private final Queue queue;
    private static final Logger LOGGER = Logger.getLogger(QueueMessenger.class.getName());

    /**
     * Constructor for QueueMessenger
     *
     * @param rabbitTemplate is a RabbitMQ template configured via application.properties
     * @param queue          is a queue object configured in the QueueConfig class
     * @see QueueConfig
     */
    @Autowired
    public QueueMessenger(RabbitTemplate rabbitTemplate, Queue queue) {
        this.queue = queue;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Sends message to an online MQTT queue.
     *
     * @param message a CameraMessage to send to the MQTT queue
     */
    @Override
    public void sendMessage(CameraMessage message) {
        Optional<String> optionalMessageString = XMLUtils.convertObjectToXML(message);
        if (optionalMessageString.isPresent()) {
            rabbitTemplate.convertAndSend(queue.getName(), optionalMessageString.get());
            LOGGER.info("Sent '" + message + "' to queue: " + queue.getName());
        } else {
            LOGGER.warning("Could not serialize message '" + message + "'!");
            LOGGER.warning("'" + message + "' did not get sent!");
        }
    }
}
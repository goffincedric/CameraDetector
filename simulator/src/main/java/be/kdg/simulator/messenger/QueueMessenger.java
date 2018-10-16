package be.kdg.simulator.messenger;

import be.kdg.simulator.utils.XMLUtils;
import be.kdg.simulator.camera.CameraMessage;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.logging.Logger;

@Component
@ConditionalOnProperty(name="messenger",havingValue = "queue")
public class QueueMessenger implements Messenger {
    private final RabbitTemplate rabbitTemplate;
    private final Queue queue;
    private static final Logger LOGGER = Logger.getLogger(QueueMessenger.class.getName());

    @Autowired
    public QueueMessenger(RabbitTemplate rabbitTemplate, Queue queue) {
        this.queue = queue;
        this.rabbitTemplate = rabbitTemplate;
    }

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
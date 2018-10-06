package be.kdg.simulator.messenger;

import be.kdg.simulator.generators.MessageGenerator;
import be.kdg.simulator.misc.XMLUtils;
import be.kdg.simulator.model.CameraMessage;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
        rabbitTemplate.convertAndSend(queue.getName(), XMLUtils.convertObjectToXML(message));
        LOGGER.info(this.getClass().getCanonicalName() + ": Sent '" + message + "' to queue: " + queue.getName());
    }


}
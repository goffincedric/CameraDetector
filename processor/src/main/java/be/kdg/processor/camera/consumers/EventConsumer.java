package be.kdg.processor.camera.consumers;

import be.kdg.processor.camera.dom.CameraMessage;
import be.kdg.processor.processor.Processor;
import be.kdg.processor.utils.XMLUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Class that receives CameraMessages from an MQTT queue (RabbitMQ) and reports the received messages to the Processor
 *
 * @author C&eacute;dric Goffin
 * @see CameraMessage
 */
@Component
public class EventConsumer {
    @Autowired
    private Processor processor;
    private static final Logger LOGGER = Logger.getLogger(EventConsumer.class.getName());

    /**
     * Method that handles the incoming messages from the MQTT queue. Can be configured via application.properties to listen to a different queue.
     *
     * @param queueMessage is a message from the queue.
     */
    @RabbitListener(queues = "${mqtt.queue_name}")
    public void receiveMessage(String queueMessage) {
        try {
            Optional<CameraMessage> optionalMessage = XMLUtils.convertXMLToObject(queueMessage, CameraMessage.class);
            if (optionalMessage.isPresent()) {
                CameraMessage message = optionalMessage.get();
                LOGGER.info(this.getClass().getCanonicalName() + ": Received '" + message + "'");
                processor.reportMessage(message);
            } else {
                LOGGER.warning("No message could be found after deserialization!");
            }
        } catch (IOException ioe) {
            LOGGER.severe(ioe.getMessage());
        }
    }
}
package be.kdg.processor.camera.consumers;

import be.kdg.processor.processor.Processor;
import be.kdg.processor.utils.XMLUtils;
import be.kdg.processor.camera.dom.CameraMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.logging.Logger;

@Component
public class EventConsumer {
    @Autowired
    private Processor processor;
    private static final Logger LOGGER = Logger.getLogger(EventConsumer.class.getName());

    @RabbitListener(queues = "${mqtt.queue_name}")
    private void receiveMessage(String queueMessage) {
        Optional<CameraMessage> optionalMessage = XMLUtils.convertXMLToObject(queueMessage, CameraMessage.class);
        if (optionalMessage.isPresent()) {
            CameraMessage message = optionalMessage.get();
            LOGGER.info(this.getClass().getCanonicalName() + ": Received '" + message + "'");
            processor.reportMessage(message);
        } else {
            LOGGER.warning("No message could be found after deserialization!");
        }
    }
}
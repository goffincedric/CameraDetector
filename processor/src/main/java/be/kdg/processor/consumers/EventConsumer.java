package be.kdg.processor.consumers;

import be.kdg.processor.misc.XMLUtils;
import be.kdg.processor.model.camera.CameraMessage;
import be.kdg.sa.services.CameraServiceProxy;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class EventConsumer {
    private static final Logger LOGGER = Logger.getLogger(EventConsumer.class.getName());

    @RabbitListener(queues = "${mqtt.queue_name}")
    private void receiveMessage(String queueMessage) {
        CameraMessage message = XMLUtils.convertXMLToObject(queueMessage, CameraMessage.class);
        LOGGER.info(this.getClass().getCanonicalName() + ": Received '" + message + "'");
    }
}
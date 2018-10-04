package be.kdg.processor.consumers;

import be.kdg.processor.Processor;
import be.kdg.processor.misc.JSONUtils;
import be.kdg.processor.misc.XMLUtils;
import be.kdg.processor.model.camera.CameraMessage;
import be.kdg.processor.model.licenseplate.Licenseplate;
import be.kdg.sa.services.CameraServiceProxy;
import be.kdg.sa.services.LicensePlateServiceProxy;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

@Component
public class EventConsumer {
    @Autowired
    private Processor processor;
    private static final Logger LOGGER = Logger.getLogger(EventConsumer.class.getName());

    @RabbitListener(queues = "${mqtt.queue_name}")
    private void receiveMessage(String queueMessage) {
        CameraMessage message = XMLUtils.convertXMLToObject(queueMessage, CameraMessage.class);
        LOGGER.info(this.getClass().getCanonicalName() + ": Received '" + message + "'");

        processor.reportMessage(message);

//        LicensePlateServiceProxy proxy = new LicensePlateServiceProxy();
//        Licenseplate licenseplate = null;
//        try {
//            licenseplate = JSONUtils.convertJSONToObject(proxy.get(message.getLicenseplate()), Licenseplate.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(licenseplate);
    }
}
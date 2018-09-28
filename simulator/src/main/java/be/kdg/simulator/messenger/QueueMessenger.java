package be.kdg.simulator.messenger;

import be.kdg.simulator.generators.MessageGenerator;
import be.kdg.simulator.model.CameraMessage;
//import com.rabbitmq.client.Channel;
//import com.rabbitmq.client.Connection;
//import com.rabbitmq.client.ConnectionFactory;
import ch.qos.logback.core.util.FixedDelay;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

@Component
@EnableScheduling
@ConditionalOnProperty(name="messenger",havingValue = "queue")
public class QueueMessenger implements Messenger {

//    @Value("${mqtt.url}")
//    private String uri;
//    @Value("${mqtt.queue_name}")
//    private String queue_name;

    private final MessageGenerator messageGenerator;

    public QueueMessenger(MessageGenerator messageGenerator) {
        this.messageGenerator = messageGenerator;
    }

    @Override
//    @Scheduled(cron = "${messenger.frequency.normal}")
//    @Scheduled(cron = "${messenger.frequency.peak}")
    @Scheduled(fixedDelay = 1000)
    public void sendMessage() {
//        sendMessage(messageGenerator.generate());
    }

    @Scheduled(fixedDelay = 1000)
    public void sendMessageFromFile() {
        //TODO
    }

//    private void sendMessage(CameraMessage message) {
//        System.out.println(uri);
//        ConnectionFactory factory = new ConnectionFactory();
//        try {
//            factory.setConnectionTimeout(30000);
//            factory.setUri(uri);
//            Connection connection = factory.newConnection();
//            Channel channel = connection.createChannel();
//            channel.queueDeclare(queue_name, true, false, false, null);
//            channel.basicPublish("", queue_name, null, XMLSerializer.convertObjectToXML(message).getBytes());
//            System.out.println(" [x] Sent '" + message + "'");
//            connection.close();
//        } catch (IOException | NoSuchAlgorithmException | URISyntaxException | KeyManagementException | TimeoutException e) {
//            e.printStackTrace();
//        }
//    }


}
package be.kdg.simulator.messenger;

import be.kdg.simulator.generators.MessageGenerator;
import be.kdg.simulator.model.CameraMessage;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@ConditionalOnProperty(name="messenger",havingValue = "queue")
public class QueueMessenger implements Messenger {

    private final MessageGenerator messageGenerator;
    private final RabbitTemplate rabbitTemplate;
    private final Queue queue;

    @Autowired
    public QueueMessenger(MessageGenerator messageGenerator, RabbitTemplate rabbitTemplate, Queue queue) {
        this.messageGenerator = messageGenerator;
        this.queue = queue;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    @Scheduled(cron = "${messenger.frequency.normal}")
    @Scheduled(cron = "${messenger.frequency.peak}")
    public void sendMessage() {
        CameraMessage message = messageGenerator.generate();
        rabbitTemplate.convertAndSend(queue.getName(), XMLSerializer.convertObjectToXML(message));
        System.out.println("[X] Sent " + message + " to queue: " + queue.getName());

        CameraMessage msg = (CameraMessage) XMLSerializer.convertXMLToObject(new String(rabbitTemplate.receive(queue.getName()).getBody()), CameraMessage.class);
        System.out.println("[X] Received: " + msg);
        System.out.println("=================");
        try {
            Thread.sleep(message.getDelay());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
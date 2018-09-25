package be.kdg.simulator.messenger;

import be.kdg.simulator.generators.FileGenerator;
import be.kdg.simulator.generators.MessageGenerator;
import be.kdg.simulator.model.CameraMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@ConditionalOnProperty(name="messenger",havingValue = "cli")
public class CommandlineMessenger implements Messenger {

    private final MessageGenerator messageGenerator;

    public CommandlineMessenger(MessageGenerator messageGenerator) {
        this.messageGenerator = messageGenerator;
    }

    @Override
    @Scheduled(cron = "${messenger.frequency.normal}")
    @Scheduled(cron = "${messenger.frequency.peak}")
    @ConditionalOnProperty(value = "generator", havingValue = "random")
    public void sendMessage() {
        System.out.println(messageGenerator.generate());
    }

    @Override
    @ConditionalOnProperty(value = "generator", havingValue = "file")
    public void sendMessageFromFile() {
        sendMessageFromFile(0);
    }

    private void sendMessageFromFile(int delay) {
        CameraMessage message = messageGenerator.generate();
        System.out.println(message);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!FileGenerator.getMessages().isEmpty())
            sendMessageFromFile(message.getDelay());
    }
}

package be.kdg.simulator.messenger;

import be.kdg.simulator.generators.FileGenerator;
import be.kdg.simulator.generators.MessageGenerator;
import be.kdg.simulator.model.CameraMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@EnableScheduling
@ConditionalOnProperty(name="messenger",havingValue = "cli")
public class CommandlineMessenger implements Messenger {

    private final MessageGenerator messageGenerator;

    public CommandlineMessenger(MessageGenerator messageGenerator) {
        this.messageGenerator = messageGenerator;
    }

    @Override
    @Scheduled(cron = "${messenger.frequency.normal}")
    @Scheduled(cron = "${messenger.frequency.peak}")
    public void sendMessage() {
        CameraMessage message = messageGenerator.generate();
        System.out.println(message);
        try {
            Thread.sleep(message.getDelay());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

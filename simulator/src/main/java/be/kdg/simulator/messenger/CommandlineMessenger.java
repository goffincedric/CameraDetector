package be.kdg.simulator.messenger;

import be.kdg.simulator.generators.MessageGenerator;
import be.kdg.simulator.model.CameraMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
    public void sendMessage() {
        System.out.println(messageGenerator.generate());
    }
}

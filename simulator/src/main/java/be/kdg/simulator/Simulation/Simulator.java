package be.kdg.simulator.Simulation;

import be.kdg.simulator.generators.MessageGenerator;
import be.kdg.simulator.messenger.Messenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Cédric Goffin
 * 21/09/2018 13:45
 */

@Component
@EnableScheduling
public class Simulator {
    private final Messenger messenger;
    private final MessageGenerator messageGenerator;

    @Autowired
    public Simulator(Messenger messenger, MessageGenerator messageGenerator) {
        this.messageGenerator = messageGenerator;
        this.messenger = messenger;
    }

    @Scheduled(cron = "${messenger.frequency.normal}")
    @Scheduled(cron = "${messenger.frequency.peak}")
    public void sendMessage() {
        messenger.sendMessage(messageGenerator.generate());
    }
}

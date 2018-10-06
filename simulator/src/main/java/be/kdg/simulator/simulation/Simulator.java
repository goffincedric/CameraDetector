package be.kdg.simulator.simulation;

import be.kdg.simulator.generators.MessageGenerator;
import be.kdg.simulator.messenger.Messenger;
import be.kdg.simulator.camera.CameraMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author Cédric Goffin
 * 21/09/2018 13:45
 */

@Component
public class Simulator {
    private final Messenger messenger;
    private final MessageGenerator messageGenerator;

    @Autowired
    public Simulator(Messenger messenger, MessageGenerator messageGenerator) {
        this.messenger = messenger;
        this.messageGenerator = messageGenerator;
    }

    public void sendMessage() {
        CameraMessage message = messageGenerator.generate();

        try {
            Thread.sleep(message.getDelay());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        messenger.sendMessage(message);
    }


}

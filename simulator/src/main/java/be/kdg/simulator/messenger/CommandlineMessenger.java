package be.kdg.simulator.messenger;

import be.kdg.simulator.camera.CameraMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Messenger class that send CameraMessages to the console window.
 *
 * @author C&eacute;dric Goffin
 * @see Messenger
 * @see CameraMessage
 */
@Component
@ConditionalOnProperty(name = "messenger", havingValue = "cli")
public class CommandlineMessenger implements Messenger {

    /**
     * Sends message to console window.
     *
     * @param message a CameraMessage to send to console window
     */
    @Override
    public void sendMessage(CameraMessage message) {
        System.out.println(message);
    }
}

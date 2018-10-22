package be.kdg.simulator.messenger;

import be.kdg.simulator.camera.CameraMessage;

/**
 * Interface for messengers
 *
 * @see QueueMessenger
 * @see CommandlineMessenger
 */
public interface Messenger {

    void sendMessage(CameraMessage message);
}

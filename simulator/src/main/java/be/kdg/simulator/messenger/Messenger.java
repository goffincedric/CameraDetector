package be.kdg.simulator.messenger;

import be.kdg.simulator.model.CameraMessage;

public interface Messenger {

    void sendMessage(CameraMessage message);
}

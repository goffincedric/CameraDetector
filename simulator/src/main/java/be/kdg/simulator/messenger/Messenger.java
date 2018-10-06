package be.kdg.simulator.messenger;

import be.kdg.simulator.camera.CameraMessage;

public interface Messenger {

    void sendMessage(CameraMessage message);
}

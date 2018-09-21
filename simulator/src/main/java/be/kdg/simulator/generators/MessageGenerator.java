package be.kdg.simulator.generators;

import be.kdg.simulator.model.CameraMessage;

import java.util.List;

public interface MessageGenerator {

    CameraMessage generate();
    List<CameraMessage> generateList();
}

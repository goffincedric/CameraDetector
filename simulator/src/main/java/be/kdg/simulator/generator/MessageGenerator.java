package be.kdg.simulator.generator;

import be.kdg.simulator.camera.CameraMessage;

import java.util.Optional;

/**
 * Interface for generators
 *
 * @see FileGenerator
 * @see ImageGenerator
 * @see RandomGenerator
 */
public interface MessageGenerator {

    Optional<CameraMessage> generate();
}

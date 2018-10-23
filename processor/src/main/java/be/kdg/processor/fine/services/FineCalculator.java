package be.kdg.processor.fine.services;

import be.kdg.processor.camera.dom.Camera;
import be.kdg.processor.camera.dom.CameraMessage;
import be.kdg.processor.fine.dom.EmissionFine;
import be.kdg.processor.fine.dom.Fine;
import be.kdg.processor.fine.dom.SpeedingFine;
import be.kdg.processor.licenseplate.dom.Licenseplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

/**
 * Helper class that helps the FineService calculate the Fine
 *
 * @author Cédric Goffin
 * @see FineService
 */
@Component
public class FineCalculator {
    /**
     * Method that calculates and creates the EmissionFine for the FineService.
     *
     * @param camera              camera that contains information for the Fine calculation
     * @param licenseplate        license plate that needs to be fined
     * @param emissionFineFactor  price of the EmissionFine
     * @param paymentDeadlineDays timeframe (of # days) in which the EmissionFine must be paid by the owner
     * @return an Optional EmissionFine
     */
    public Optional<Fine> calcEmissionFine(Camera camera, Licenseplate licenseplate, double emissionFineFactor, int paymentDeadlineDays) {
        return Optional.of(
                new EmissionFine(
                        emissionFineFactor,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(paymentDeadlineDays),
                        licenseplate,
                        licenseplate.getEuroNumber(),
                        camera.getEuroNorm())
        );
    }

    /**
     * Method that calculates and creates the SpeedingFine for the FineService.
     * Speeding fine amount gets calculated according to current laws: https://www.wegcode.be/boetetarieven
     *
     * @param messagePair         pair of CameraMessages that contain information for the Fine calculation
     * @param camera              camera that contains information for the Fine calculation
     * @param licenseplate        license plate that needs to be fined
     * @param speedFineFactorSlow Fine factor that is used for the price when the speeding violation happened in a zone with a speed limit <= 50 km/h
     * @param speedFineFactorFast Fine factor that is used for the price when the speeding violation happened in a zone with a speed limit > 50 km/h
     * @param paymentDeadlineDays timeframe (of # days) in which the EmissionFine must be paid by the owner
     * @return an Optional SpeedingFine. Optional will be empty when the speed is lower than the speed limit.
     */
    public Optional<Fine> calcSpeedFine(Map.Entry<CameraMessage, CameraMessage> messagePair, Camera camera, Licenseplate licenseplate, double speedFineFactorSlow, double speedFineFactorFast, int paymentDeadlineDays) {
        // Puntje B (zone 30/50 kan je zien a.d.h.v. allowed speed van camera)
        // https://www.wegcode.be/boetetarieven
        Double speed = calcSpeed(camera.getSegment().getDistance(), messagePair.getKey().getTimestamp(), messagePair.getValue().getTimestamp());
        if (speed > camera.getSegment().getSpeedLimit()) {
            return Optional.of(new SpeedingFine(
                    (speed.intValue() - camera.getSegment().getSpeedLimit()) * ((camera.getSegment().getSpeedLimit() <= 50) ? speedFineFactorSlow : speedFineFactorFast),
                    LocalDateTime.now(),
                    LocalDateTime.now().plusDays(paymentDeadlineDays),
                    licenseplate,
                    speed.intValue(),
                    camera.getSegment().getSpeedLimit())
            );
        }
        return Optional.empty();
    }

    private Double calcSpeed(double distance, LocalDateTime timestamp1, LocalDateTime timestamp2) {
        return ((distance / (ChronoUnit.MILLIS.between(timestamp1, timestamp2) / 1000D)) * 3.6D);
    }
}

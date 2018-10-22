package be.kdg.processor.fine;

import be.kdg.processor.camera.dom.Camera;
import be.kdg.processor.camera.dom.CameraMessage;
import be.kdg.processor.fine.dom.EmissionFine;
import be.kdg.processor.fine.dom.Fine;
import be.kdg.processor.fine.dom.SpeedingFine;
import be.kdg.processor.licenseplate.dom.Licenseplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

/**
 * @author Cédric Goffin
 * 04/10/2018 16:18
 */
@Component
public class FineDetector {
    @Value("${fine.emission.timeframe_days}")
    private int emissionTimeFrameDays;
    @Value("${fine.emission.fineFactor}")
    private double emissionFineFactor;
    @Value("${fine.speed.fineFactor.slow}")
    private double speedFineFactorSlow;
    @Value("${fine.speed.fineFactor.fast}")
    private double speedFineFactorFast;
    @Value("${fine.paymentDeadlineDays}")
    private int paymentDeadlineDays;

    public Optional<Fine> checkEmissionFine(CameraMessage message, Camera camera, Licenseplate licenseplate) {
        if (camera.getEuroNorm() > licenseplate.getEuroNumber()) {
            if (licenseplate.getFines().stream().noneMatch(f -> f.getTimestamp().isAfter(message.getTimestamp().minusDays(emissionTimeFrameDays)))) {
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
        }
        return Optional.empty();
    }

    public Optional<Fine> checkSpeedFine(Map.Entry<CameraMessage, CameraMessage> messagePair, Camera camera, Licenseplate licenseplate) {
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

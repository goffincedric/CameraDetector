package be.kdg.processor.fine;

import be.kdg.processor.camera.dom.Camera;
import be.kdg.processor.camera.dom.CameraMessage;
import be.kdg.processor.fine.dom.EmissionFine;
import be.kdg.processor.fine.dom.Fine;
import be.kdg.processor.licenseplate.dom.Licenseplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.FileNameMap;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    @Value("${fine.emission.paymentDeadlineDays}")
    private int paymentDeadlineDays;



















    public List<Fine> checkFines(Map<CameraMessage, Camera> messageMap, Licenseplate licenseplate) {
        List<Fine> fines = new ArrayList<>();

//        messageMap.forEach((message, camera) -> {
//            switch (camera.getCameraType()) {
//                case EMISSION:
//                    checkEmissionFine(message, camera, licenseplate).ifPresent(f -> {
//                        licenseplate.getFines().add(f);
//                        fines.add(f);
//                    });
//                    break;
//                case SPEED:
//                    checkSpeedFine(messages, camera, licenseplate).ifPresent(fines::add);
//                    break;
//                case SPEED_EMISSION:
//                    checkEmissionFine(message, camera, licenseplate).ifPresent(fines::add);
//                    checkSpeedFine(message, camera, licenseplate).ifPresent(fines::add);
//                    break;
//            }
//        });


        licenseplate.getFines().addAll(fines);

        //TODO
        return fines;
    }

    public Optional<Fine> checkEmissionFine(CameraMessage message, Camera camera, Licenseplate licenseplate) {
        if (camera.getEuroNorm() > licenseplate.getEuroNumber()) {
            if (licenseplate.getFines().stream().anyMatch(f -> f.getTimestamp().isAfter(message.getTimestamp().minusDays(emissionTimeFrameDays)))) {
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

    private Optional<Fine> checkSpeedFine(List<CameraMessage> message, Camera camera, Licenseplate licenseplate) {
        // Puntje B (zone 30/50 kan je zien a.d.h.v. allowed speed van camera)
        // https://www.wegcode.be/boetetarieven
        double snelheid = calcSpeed();
        //TODO
        return Optional.empty();
    }

    private double calcSpeed() {
        //TODO
        return 0;
    }
}

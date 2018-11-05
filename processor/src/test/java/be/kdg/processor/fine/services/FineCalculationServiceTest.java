package be.kdg.processor.fine.services;

import be.kdg.processor.camera.dom.Camera;
import be.kdg.processor.camera.dom.CameraMessage;
import be.kdg.processor.camera.services.CameraServiceAdapter;
import be.kdg.processor.fine.dom.EmissionFine;
import be.kdg.processor.fine.dom.Fine;
import be.kdg.processor.fine.dom.SpeedingFine;
import be.kdg.processor.licenseplate.services.LicenseplateServiceAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author C&eacute;dric Goffin
 * 17/10/2018 15:20
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class FineCalculationServiceTest {

    @Autowired
    private CameraServiceAdapter cameraServiceAdapter;
    @Autowired
    private LicenseplateServiceAdapter licenseplateServiceAdapter;
    @Autowired
    private FineCalculationService fineCalculationService;

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

    @Test
    public void checkEmissionFine() throws Exception {
        CameraMessage cameraMessage = new CameraMessage(3, null, "1-ABC-123", LocalDateTime.now(), 100);
        Optional<Fine> optionalFine = fineCalculationService.calcEmissionFine(
                cameraServiceAdapter.getCamera(cameraMessage.getCameraId()).get(),
                licenseplateServiceAdapter.getLicensePlate(cameraMessage.getLicenseplate()).get(),
                emissionFineFactor,
                paymentDeadlineDays
        );
        Assert.assertTrue(optionalFine.isPresent());
        Fine fine = optionalFine.get();
        Assert.assertTrue(fine instanceof EmissionFine);
        Assert.assertEquals(fine.getAmount(), emissionFineFactor, 0.0);
    }

    @Test
    public void checkSpeedFine() throws Exception {
        Map.Entry<CameraMessage, CameraMessage> speedpair = Map.entry(
                new CameraMessage(1, null, "4-ABC-123", LocalDateTime.now().withNano(0), 0),
                new CameraMessage(2, null, "4-ABC-123", LocalDateTime.now().withNano(0).plusSeconds(120), 120000)
        );

        Optional<Fine> optionalFine = fineCalculationService.calcSpeedFine(
                speedpair,
                new LinkedList() {{addFirst(cameraServiceAdapter.getCamera(speedpair.getKey().getCameraId()).get()); addLast(cameraServiceAdapter.getCamera(speedpair.getValue().getCameraId()).get());}},
                licenseplateServiceAdapter.getLicensePlate(speedpair.getKey().getLicenseplate()).get(),
                speedFineFactorSlow,
                speedFineFactorFast,
                paymentDeadlineDays
        );


        Camera camera = cameraServiceAdapter.getCamera(speedpair.getKey().getCameraId()).get();
        Assert.assertTrue(optionalFine.isPresent());
        Fine fine = optionalFine.get();
        Assert.assertTrue(fine instanceof SpeedingFine);
        double amount = (Double.valueOf(((camera.getSegment().getDistance() / (ChronoUnit.MILLIS.between(speedpair.getKey().getTimestamp(), speedpair.getValue().getTimestamp()) / 1000D)) * 3.6D)).intValue() - camera.getSegment().getSpeedLimit()) * ((camera.getSegment().getSpeedLimit() <= 50) ? speedFineFactorSlow : speedFineFactorFast);
        Assert.assertEquals(fine.getAmount(), amount, 0.0);
    }
}
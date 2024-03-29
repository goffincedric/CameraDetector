package be.kdg.processor.fine.services;

import be.kdg.processor.camera.dom.CameraMessage;
import be.kdg.processor.fine.dom.EmissionFine;
import be.kdg.processor.fine.dom.Fine;
import be.kdg.processor.fine.dom.SpeedingFine;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author C&eacute;dric Goffin
 * 16/10/2018 16:28
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FineDetectionServiceTests {
    @Autowired
    private FineDetectionService fineDetectionService;

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
    public void processSpeedingFines() {
        String speedingLicensePlate = "4-ABC-123";
        String nonSpeedingLicensePlate = "5-ABC-123";
        List<CameraMessage> messages = new ArrayList<>() {{
            add(new CameraMessage(1, null, speedingLicensePlate, LocalDateTime.now().withNano(0), 0));
            add(new CameraMessage(2, null, speedingLicensePlate, LocalDateTime.now().withNano(0).plusSeconds(120), 120000));
            add(new CameraMessage(1, null, nonSpeedingLicensePlate, LocalDateTime.now().withNano(0), 0));
            add(new CameraMessage(2, null, nonSpeedingLicensePlate, LocalDateTime.now().withNano(0).plusSeconds(240), 240000));
        }};

        Map.Entry<List<Fine>, List<CameraMessage>> fineResult = fineDetectionService.processSpeedingFines(messages);

        Assert.assertEquals(1, fineResult.getKey().size());
        Assert.assertEquals(0, fineResult.getValue().size());

        fineResult.getKey().forEach(fine -> Assert.assertTrue(fine instanceof SpeedingFine && fine.getLicenseplateId().equals(speedingLicensePlate)));
    }

    @Test
    public void processEmissionFines() {
        List<CameraMessage> messages = new ArrayList<>() {{
            add(new CameraMessage(3, null, "1-ABC-123", LocalDateTime.now(), 100));
            add(new CameraMessage(3, null, "1-ABC-123", LocalDateTime.now(), 100));
        }};

        Map.Entry<List<Fine>, List<CameraMessage>> fineResult = fineDetectionService.processEmissionFines(messages);

        Assert.assertEquals(1, fineResult.getKey().size());
        Assert.assertEquals(0, fineResult.getValue().size());

        fineResult.getKey().forEach(fine -> Assert.assertTrue(fine instanceof EmissionFine));
    }

}

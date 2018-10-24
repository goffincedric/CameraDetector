package be.kdg.processor.fine;

import be.kdg.processor.camera.dom.CameraMessage;
import be.kdg.processor.camera.services.CameraServiceAdapter;
import be.kdg.processor.fine.dom.EmissionFine;
import be.kdg.processor.fine.dom.Fine;
import be.kdg.processor.fine.dom.SpeedingFine;
import be.kdg.processor.fine.services.FineCalculator;
import be.kdg.processor.fine.services.FineService;
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
public class FineServiceTests {
    @Autowired
    private FineService fineService;

    @Autowired
    private CameraServiceAdapter cameraServiceAdapter;
    @Autowired
    private LicenseplateServiceAdapter licenseplateServiceAdapter;
    @Autowired
    private FineCalculator fineCalculator;

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
        List<CameraMessage> messages = new ArrayList<>() {{
            add(new CameraMessage(1, null, "4-ABC-123", LocalDateTime.now().withNano(0), 0));
            add(new CameraMessage(2, null, "4-ABC-123", LocalDateTime.now().withNano(0).plusSeconds(120), 120000));
        }};

        Map.Entry<List<Fine>, List<CameraMessage>> fineResult = fineService.processSpeedingFines(messages);

        Assert.assertEquals(1, fineResult.getKey().size());
        Assert.assertEquals(0, fineResult.getValue().size());

        fineResult.getKey().forEach(fine -> Assert.assertTrue(fine instanceof SpeedingFine));
    }

    @Test
    public void processEmissionFines() {
        List<CameraMessage> messages = new ArrayList<>() {{
            add(new CameraMessage(3, null, "1-ABC-123", LocalDateTime.now(), 100));
        }};

        Map.Entry<List<Fine>, List<CameraMessage>> fineResult = fineService.processEmissionFines(messages);

        Assert.assertEquals(1, fineResult.getKey().size());
        Assert.assertEquals(0, fineResult.getValue().size());

        fineResult.getKey().forEach(fine -> Assert.assertTrue(fine instanceof EmissionFine));
    }

}

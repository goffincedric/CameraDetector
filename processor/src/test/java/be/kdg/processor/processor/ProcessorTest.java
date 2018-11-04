package be.kdg.processor.processor;

import be.kdg.processor.camera.consumers.EventConsumer;
import be.kdg.processor.camera.dom.CameraMessage;
import be.kdg.processor.fine.dom.EmissionFine;
import be.kdg.processor.fine.dom.Fine;
import be.kdg.processor.fine.dom.SpeedingFine;
import be.kdg.processor.fine.services.FineService;
import be.kdg.processor.processor.services.SettingService;
import be.kdg.processor.utils.XMLUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.fail;

/**
 * @author Cédric Goffin
 * 03/11/2018 21:58
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ProcessorTest {
    @Autowired
    private SettingService settingService;
    @Autowired
    private EventConsumer eventConsumer;
    @Autowired
    private FineService fineService;
    @Autowired
    private Processor processor;

    @Test
    public void checkMessages() {
        String speedingLicensePlate = "4-ABC-123";
        String emissionLicensePlate = "1-ABC-123";
        String badLicensePlate = "1-ERR-123";
        List<CameraMessage> messages = new ArrayList<>() {{
            add(new CameraMessage(1, null, speedingLicensePlate, LocalDateTime.now().withNano(0), 0));
            add(new CameraMessage(2, null, speedingLicensePlate, LocalDateTime.now().withNano(0).plusSeconds(120), 120000));
            add(new CameraMessage(3, null, emissionLicensePlate, LocalDateTime.now(), 100));
            add(new CameraMessage(3, null, badLicensePlate, LocalDateTime.now(), 100));
        }};
        messages.forEach(m -> {
            try {
                XMLUtils.convertObjectToXML(m).ifPresent(s -> eventConsumer.receiveMessage(s));
            } catch (JsonProcessingException e) {
                fail();
            }
        });

        processor.CheckMessages();

        List<Fine> emissionFines = fineService.getFinesByLicenseplate(emissionLicensePlate);
        Assert.assertTrue(emissionFines.size() >= 1);
        Assert.assertTrue(emissionFines.stream().anyMatch(fine ->
                fine instanceof EmissionFine && fine.getLicenseplateId().equals(emissionLicensePlate))
        );

        List<Fine> speedingFines = fineService.getFinesByLicenseplate(speedingLicensePlate);
        Assert.assertTrue(emissionFines.size() >= 1);
        Assert.assertTrue(speedingFines.stream().anyMatch(fine ->
                fine instanceof SpeedingFine && fine.getLicenseplateId().equals(speedingLicensePlate))
        );
    }
}
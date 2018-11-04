package be.kdg.processor.utils;

import be.kdg.processor.camera.dom.CameraMessage;
import be.kdg.processor.processor.dom.StringSetting;
import be.kdg.processor.processor.services.SettingService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author Cédric Goffin
 * 03/11/2018 18:40
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CSVUtilsTest {
    @Autowired
    private SettingService settingService;

    @Test
    public void writeMessage() {
        CameraMessage cameraMessage = new CameraMessage(999, null, "1-ERR-123", LocalDateTime.now(), 0);
        Optional<StringSetting> optionalLogPath = settingService.getStringSetting("logPath");
        if (optionalLogPath.isPresent()) {
            String logPath = optionalLogPath.get().getStringValue();
            logPath = CSVUtils.writeMessage(cameraMessage, logPath);

            File logFile = new File(logPath);
            Assert.assertTrue(logFile.exists());
            Assert.assertTrue(logFile.delete());
        } else {
            fail();
        }
    }
}
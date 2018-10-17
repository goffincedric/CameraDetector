package be.kdg.processor.fine;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @author Cédric Goffin
 * 17/10/2018 15:20
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FineDetectorTest {

    @Autowired
    private FineDetector fineDetector;

    @Test
    public void checkEmissionFine() {
    }

    @Test
    public void checkSpeedFine() {
    }
}
package be.kdg.processor.fine;

import be.kdg.processor.fine.services.FineService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Cédric Goffin
 * 16/10/2018 16:28
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FineServiceTests {
    @Autowired
    private FineService fineService;

    @Test
    public void testFineService() {

    }

}

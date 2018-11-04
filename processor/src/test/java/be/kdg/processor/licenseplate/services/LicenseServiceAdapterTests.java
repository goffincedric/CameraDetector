package be.kdg.processor.licenseplate.services;

import be.kdg.processor.licenseplate.dom.Licenseplate;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

/**
 * @author C&eacute;dric Goffin
 * 16/10/2018 16:15
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class LicenseServiceAdapterTests {

    @Autowired
    private LicenseplateServiceAdapter licenseplateServiceAdapter;

    @Test
    public void testLicenseplateService() throws Exception {
        String licenseplateId = String.format("%s-%S-%s", RandomStringUtils.random(1, "12345678"), RandomStringUtils.random(3, true, false), RandomStringUtils.random(3, false, true));
        Optional<Licenseplate> optionalLicenseplate = licenseplateServiceAdapter.getLicensePlate(licenseplateId);
        Assert.assertTrue(optionalLicenseplate.isPresent());
        Assert.assertTrue(optionalLicenseplate.get().getPlateId().equalsIgnoreCase(licenseplateId));
    }
}

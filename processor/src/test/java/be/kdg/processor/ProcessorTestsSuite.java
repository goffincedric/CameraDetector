package be.kdg.processor;

import be.kdg.processor.camera.CameraServiceAdapterTests;
import be.kdg.processor.licenseplate.LicenseServiceAdapterTests;
import be.kdg.processor.openalpr.CloudALPRServiceTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CameraServiceAdapterTests.class,
        LicenseServiceAdapterTests.class,
        CloudALPRServiceTests.class
})
public class ProcessorTestsSuite {
}

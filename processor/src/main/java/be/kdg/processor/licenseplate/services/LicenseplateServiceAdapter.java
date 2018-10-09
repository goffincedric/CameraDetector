package be.kdg.processor.licenseplate.services;

import be.kdg.processor.licenseplate.Licenseplate;
import be.kdg.processor.utils.JSONUtils;
import be.kdg.sa.services.LicensePlateNotFoundException;
import be.kdg.sa.services.LicensePlateServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Cédric Goffin
 * 09/10/2018 13:36
 */
@Component
public class LicenseplateServiceAdapter {
    private static final Logger LOGGER = Logger.getLogger(LicenseplateServiceAdapter.class.getName());

    private final LicensePlateServiceProxy licensePlateServiceProxy;

    @Autowired
    public LicenseplateServiceAdapter(LicensePlateServiceProxy licensePlateServiceProxy) {
        this.licensePlateServiceProxy = licensePlateServiceProxy;
    }

    public Licenseplate getLicensePlate(String licensePlateId) {
        Licenseplate licenseplate = null;
        try {
            licenseplate = JSONUtils.convertJSONToObject(licensePlateServiceProxy.get(licensePlateId), Licenseplate.class);
        } catch (IOException e) {
            LOGGER.severe("Unable to deserialize licenseplate with id: " + licensePlateId);
            e.printStackTrace();
        } catch (LicensePlateNotFoundException lnfe) {
            LOGGER.severe("Could not find license plate with id: " + licensePlateId);
        }
        return licenseplate;
    }
}

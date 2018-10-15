package be.kdg.processor.licenseplate.services;

import be.kdg.processor.camera.dom.CameraMessage;
import be.kdg.processor.licenseplate.dom.Licenseplate;
import be.kdg.processor.licenseplate.misc.CloudALPRService;
import be.kdg.processor.utils.JSONUtils;
import be.kdg.sa.services.LicensePlateNotFoundException;
import be.kdg.sa.services.LicensePlateServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * @author Cédric Goffin
 * 09/10/2018 13:36
 */
@Component
@Transactional
public class LicenseplateServiceAdapter {
    private static final Logger LOGGER = Logger.getLogger(LicenseplateServiceAdapter.class.getName());

    private final LicensePlateServiceProxy licensePlateServiceProxy;
    private final CloudALPRService cloudALPRService;

    @Autowired
    public LicenseplateServiceAdapter(LicensePlateServiceProxy licensePlateServiceProxy, CloudALPRService cloudALPRService) {
        this.licensePlateServiceProxy = licensePlateServiceProxy;
        this.cloudALPRService = cloudALPRService;
    }

    public Optional<Licenseplate> getLicensePlate(String licensePlateId) {
        Optional<Licenseplate> licenseplate;
        try {
            licenseplate = JSONUtils.convertJSONToObject(licensePlateServiceProxy.get(licensePlateId), Licenseplate.class);
        } catch (IOException e) {
            LOGGER.severe("Unable to deserialize licenseplate with id: " + licensePlateId);
            licenseplate = Optional.empty();
        } catch (LicensePlateNotFoundException lnfe) {
            LOGGER.severe("Could not find license plate with id: " + licensePlateId);
            licenseplate = Optional.empty();
        }
        return licenseplate;
    }

    public Optional<Licenseplate> getLicensePlate(byte[] data) {
        String licenseplate = cloudALPRService.getLicenseplate(data);
        return getLicensePlate(licenseplate);
    }

    public Optional<Licenseplate> getLicensePlate(CameraMessage cameraMessage) {
        if (cameraMessage.getLicenseplate() != null) {
            return getLicensePlate(cameraMessage.getLicenseplate());
        } else if (cameraMessage.getCameraImage() != null) {
            return getLicensePlate(cameraMessage.getCameraImage());
        }
        return Optional.empty();
    }
}

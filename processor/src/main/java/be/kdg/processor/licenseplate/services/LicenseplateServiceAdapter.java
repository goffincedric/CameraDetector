package be.kdg.processor.licenseplate.services;

import be.kdg.processor.camera.dom.CameraMessage;
import be.kdg.processor.licenseplate.dom.Licenseplate;
import be.kdg.processor.licenseplate.repository.LicenseplateRepository;
import be.kdg.processor.utils.JSONUtils;
import be.kdg.sa.services.InvalidLicensePlateException;
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

    private final CloudALPRService cloudALPRService;
    private final LicensePlateServiceProxy licensePlateServiceProxy;
    private final LicenseplateRepository licenseplateRepository;

    @Autowired
    public LicenseplateServiceAdapter(LicensePlateServiceProxy licensePlateServiceProxy, CloudALPRService cloudALPRService, LicenseplateRepository licenseplateRepository) {
        this.licensePlateServiceProxy = licensePlateServiceProxy;
        this.cloudALPRService = cloudALPRService;
        this.licenseplateRepository = licenseplateRepository;
    }

    public Optional<Licenseplate> getLicensePlate(String licensePlateId) throws Exception {
        Optional<Licenseplate> licenseplate = licenseplateRepository.findById(licensePlateId);
        if (!licenseplate.isPresent()) {
            try {
                licenseplate = JSONUtils.convertJSONToObject(licensePlateServiceProxy.get(licensePlateId), Licenseplate.class);
                if (licenseplate.isPresent()) {
                    licenseplate = Optional.of(saveLicenseplate(licenseplate.get()));
                }
            } catch (IOException e) {
                LOGGER.severe("Unable to deserialize licenseplate with id: " + licensePlateId);
                licenseplate = Optional.empty();
                throw new Exception("Error while getting license plate with id: " + licensePlateId);
            } catch (LicensePlateNotFoundException lnfe) {
                LOGGER.severe("Could not find license plate with id: " + licensePlateId);
                licenseplate = Optional.empty();
                throw new Exception("Error while getting license plate with id: " + licensePlateId);
            } catch (InvalidLicensePlateException ile) {
                LOGGER.severe("Invalid license plate: " + licensePlateId);
                licenseplate = Optional.empty();
                throw new Exception("Error while getting license plate with id: " + licensePlateId);
            }
        }

        return licenseplate;
    }

    public Optional<Licenseplate> getLicensePlate(byte[] data) {
        String licenseplate = cloudALPRService.getLicenseplate(data);
        try {
            return getLicensePlate(licenseplate);
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Licenseplate> getLicensePlate(CameraMessage cameraMessage) {
        try {
            if (cameraMessage.getLicenseplate() != null) {
                return getLicensePlate(cameraMessage.getLicenseplate());
            } else if (cameraMessage.getCameraImage() != null) {
                return getLicensePlate(cameraMessage.getCameraImage());
            }
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }
        return Optional.empty();
    }

    public Licenseplate saveLicenseplate(Licenseplate licenseplate) {
        return licenseplateRepository.save(licenseplate);
    }
}

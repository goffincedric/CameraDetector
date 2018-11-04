package be.kdg.processor.licenseplate.services;

import be.kdg.processor.licenseplate.dom.Licenseplate;
import be.kdg.processor.licenseplate.repository.LicenseplateRepository;
import be.kdg.processor.utils.JSONUtils;
import be.kdg.sa.services.InvalidLicensePlateException;
import be.kdg.sa.services.LicensePlateNotFoundException;
import be.kdg.sa.services.LicensePlateServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Service used to manipulate Camera information from the H2 in-memory database or from an external CameraServiceProxy.
 *
 * @author C&eacute;dric Goffin
 * @see LicensePlateServiceProxy
 * @see LicenseplateRepository
 */
@Component
@Transactional
@EnableCaching
public class LicenseplateServiceAdapter {
    private final CloudALPRService cloudALPRService;
    private final LicensePlateServiceProxy licensePlateServiceProxy;
    private final LicenseplateRepository licenseplateRepository;

    /**
     * Constructor used by Spring framework to initialize the service as a bean
     *
     * @param licensePlateServiceProxy is an external LicensePlateServiceProxy which can be contacted to get information about Licenseplates
     * @param cloudALPRService         is a service that contacts the OpenALPR API to recognise a license plate id from an image
     * @param licenseplateRepository   is the repository that has access to the H2 in-memory database
     */
    @Autowired
    public LicenseplateServiceAdapter(LicensePlateServiceProxy licensePlateServiceProxy, CloudALPRService cloudALPRService, LicenseplateRepository licenseplateRepository) {
        this.licensePlateServiceProxy = licensePlateServiceProxy;
        this.cloudALPRService = cloudALPRService;
        this.licenseplateRepository = licenseplateRepository;
    }

    /**
     * Gets information about a camera.
     *
     * @param licensePlateId a string containing the license plate id
     * @return an Optional Licenseplate. Is empty when no Licenseplate could be found or when an error occurred.
     * @throws Exception when a problem occurred during deserialization, when an invalid license plate id was supplied or when no license plate could be find
     */
    @Cacheable("licenseplate")
    public Optional<Licenseplate> getLicensePlate(String licensePlateId) throws Exception {
        Optional<Licenseplate> optionalLicenseplate = licenseplateRepository.findById(licensePlateId);
        if (!optionalLicenseplate.isPresent()) {
            try {
                optionalLicenseplate = JSONUtils.convertJSONToObject(licensePlateServiceProxy.get(licensePlateId), Licenseplate.class);
                if (optionalLicenseplate.isPresent()) {
                    optionalLicenseplate = Optional.of(saveLicenseplate(optionalLicenseplate.get()));
                }
            } catch (IOException | LicensePlateNotFoundException | InvalidLicensePlateException e) {
                throw new Exception(e.getMessage(), e);
            }
        }

        return optionalLicenseplate;
    }

    /**
     * Gets information about a camera.
     *
     * @param data is an array of bytes that represents the image to be analysed
     * @return an Optional Licenseplate. Is empty when no Licenseplate could be found or when an error occurred.
     * @throws Exception when no license plate could be recognised from image
     */
    @Cacheable("licenseplate")
    public Optional<Licenseplate> getLicensePlate(byte[] data) throws Exception {
        return getLicensePlate(cloudALPRService.getLicenseplate(data));
    }

    /**
     * Creates a new camera in the repository
     *
     * @param licenseplate the Licenseplate to persist to the database
     * @return the persisted Licenseplate from the repository
     */
    public Licenseplate saveLicenseplate(Licenseplate licenseplate) {
        return licenseplateRepository.save(licenseplate);
    }
}

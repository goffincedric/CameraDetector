package be.kdg.processor.fine.services;

import be.kdg.processor.camera.dom.Camera;
import be.kdg.processor.camera.dom.CameraMessage;
import be.kdg.processor.camera.dom.CameraType;
import be.kdg.processor.camera.services.CameraServiceAdapter;
import be.kdg.processor.fine.FineDetector;
import be.kdg.processor.fine.dom.Fine;
import be.kdg.processor.fine.repository.FineRepository;
import be.kdg.processor.licenseplate.dom.Licenseplate;
import be.kdg.processor.licenseplate.services.LicenseplateServiceAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Cédric Goffin
 * 12/10/2018 15:21
 */
@Service
@Transactional
public class FineService {
    private final CameraServiceAdapter cameraServiceAdapter;
    private final LicenseplateServiceAdapter licenseplateServiceAdapter;

    private final FineRepository fineRepository;
    private final FineDetector fineDetector;

    @Autowired
    public FineService(CameraServiceAdapter cameraServiceAdapter, LicenseplateServiceAdapter licenseplateServiceAdapter, FineRepository fineRepository, FineDetector fineDetector) {
        this.cameraServiceAdapter = cameraServiceAdapter;
        this.licenseplateServiceAdapter = licenseplateServiceAdapter;
        this.fineRepository = fineRepository;
        this.fineDetector = fineDetector;
    }

    public List<Fine> saveFines(List<Fine> fines) {
        return fineRepository.saveAll(fines);
    }

    public List<CameraMessage> processFines(List<CameraMessage> messages) {
        // Filter out speedmessages
        List<CameraMessage> speedMessages = cameraServiceAdapter.getMessagesFromTypes(messages, List.of(CameraType.SPEED, CameraType.SPEED_EMISSION));
        // Filter out emissionmessages
        List<CameraMessage> emissionMessages = cameraServiceAdapter.getMessagesFromTypes(messages, List.of(CameraType.EMISSION, CameraType.SPEED_EMISSION));

        // Process emissionMessages
        List<CameraMessage> unprocessed = new ArrayList<>();
        List<Fine> emissionFines = emissionMessages.stream()
                .map(m -> {
                    Optional<Camera> optionalCamera = cameraServiceAdapter.getCamera(m.getCameraId());
                    if (optionalCamera.isPresent()) {
                        Camera camera = optionalCamera.get();

                        Optional<Licenseplate> optionalLicenseplate = licenseplateServiceAdapter.getLicensePlate(m);
                        if (optionalLicenseplate.isPresent()) {
                            Licenseplate licenseplate = optionalLicenseplate.get();
                            return fineDetector.checkEmissionFine(m, camera, licenseplate);
                        }
                    }
                    unprocessed.add(m);
                    return Optional.empty();
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(f -> (Fine)f)
                .collect(Collectors.toList());

        // TODO: filter out duplicates

        return null;

    }
}

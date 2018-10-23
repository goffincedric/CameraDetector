package be.kdg.processor.processor.services;

import be.kdg.processor.fine.dto.fineSettings.FineSettingsDTO;
import be.kdg.processor.processor.dom.*;
import be.kdg.processor.processor.dto.ProcessorSettingsDTO;
import be.kdg.processor.processor.repository.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for the processor package. Manages Settings for the Processor and Fine calculation.
 *
 * @author Cédric Goffin
 */
@Service
@Transactional
public class SettingService {
    private final SettingRepository settingRepository;

    /**
     * Constructor used by Spring framework to initialize the service as a bean
     *
     * @param settingRepository the repository that has access to the H2 in-memory database
     */
    @Autowired
    public SettingService(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    /**
     * Gets information about a setting.
     *
     * @param settingName string with the unique name of the setting
     * @return an Optional Setting. Will be empty when no setting with the supplied name was found.
     */
    public Optional<Setting> getSetting(String settingName) {
        return settingRepository.findBySettingName(settingName);
    }

    /**
     * Gets information about a setting with a Boolean value.
     *
     * @param settingName string with the unique name of the setting
     * @return an Optional BoolSetting. Will be empty when no setting with the supplied name was found.
     */
    public Optional<BoolSetting> getBoolSetting(String settingName) {
        Optional<Setting> optionalSetting = getSetting(settingName);
        if (optionalSetting.isPresent()) {
            if (optionalSetting.get() instanceof BoolSetting) {
                return Optional.of((BoolSetting) optionalSetting.get());
            }
        }
        return Optional.empty();
    }

    /**
     * Gets information about a setting with a Double value.
     *
     * @param settingName string with the unique name of the setting
     * @return an Optional DoubleSetting. Will be empty when no setting with the supplied name was found.
     */
    public Optional<DoubleSetting> getDoubleSetting(String settingName) {
        Optional<Setting> optionalSetting = getSetting(settingName);
        if (optionalSetting.isPresent()) {
            if (optionalSetting.get() instanceof DoubleSetting) {
                return Optional.of((DoubleSetting) optionalSetting.get());
            }
        }
        return Optional.empty();
    }

    /**
     * Gets information about a setting with an Integer value.
     *
     * @param settingName string with the unique name of the setting
     * @return an Optional IntSetting. Will be empty when no setting with the supplied name was found.
     */
    public Optional<IntSetting> getIntSetting(String settingName) {
        Optional<Setting> optionalSetting = getSetting(settingName);
        if (optionalSetting.isPresent()) {
            if (optionalSetting.get() instanceof IntSetting) {
                return Optional.of((IntSetting) optionalSetting.get());
            }
        }
        return Optional.empty();
    }

    /**
     * Gets information about a setting with a String value.
     *
     * @param settingName string with the unique name of the setting
     * @return an Optional StringSetting. Will be empty when no setting with the supplied name was found.
     */
    public Optional<StringSetting> getStringSetting(String settingName) {
        Optional<Setting> optionalSetting = getSetting(settingName);
        if (optionalSetting.isPresent()) {
            if (optionalSetting.get() instanceof StringSetting) {
                return Optional.of((StringSetting) optionalSetting.get());
            }
        }
        return Optional.empty();
    }

    /**
     * Gets a ProcessorSettingsDTO with the current settings for the processor from the repository.
     *
     * @return a ProcessorSettingsDTO with the current settings from the repository
     */
    public ProcessorSettingsDTO getProcessorSettingsDTO() {
        return new ProcessorSettingsDTO(
                getIntSetting("retries").get().getIntValue(),
                getBoolSetting("logFailed").get().getBoolValue(),
                getStringSetting("logPath").get().getStringValue()
        );
    }

    /**
     * Gets a FineSettingsDTO with the current settings for Fine calculation from the repository.
     *
     * @return a FineSettingsDTO with the current settings from the repository
     */
    public FineSettingsDTO getFineSettingsDTO() {
        return new FineSettingsDTO(
                getIntSetting("emissionTimeFrameDays").get().getIntValue(),
                getDoubleSetting("emissionFineFactor").get().getDoubleValue(),
                getDoubleSetting("speedFineFactorSlow").get().getDoubleValue(),
                getDoubleSetting("speedFineFactorFast").get().getDoubleValue(),
                getIntSetting("paymentDeadlineDays").get().getIntValue()
        );
    }

    /**
     * Saves an updated FineSettingsDTO to the repository
     *
     * @param fineSettingsDTO DTO with updated settings for Fine calculation
     * @return an updated FineSettingsDTO object
     */
    public FineSettingsDTO saveFineSettingsDTO(FineSettingsDTO fineSettingsDTO) {
        IntSetting emissionTimeFrameDaysSetting = getIntSetting("emissionTimeFrameDays").get();
        DoubleSetting emissionFineFactorSetting = getDoubleSetting("emissionFineFactor").get();
        DoubleSetting speedFineFactorSlowSetting = getDoubleSetting("speedFineFactorSlow").get();
        DoubleSetting speedFineFactorFastSetting = getDoubleSetting("speedFineFactorFast").get();
        IntSetting paymentDeadlineDaysSetting = getIntSetting("paymentDeadlineDays").get();

        emissionTimeFrameDaysSetting.setIntValue(fineSettingsDTO.getEmissionTimeframeDays());
        emissionFineFactorSetting.setDoubleValue(fineSettingsDTO.getEmissionFineFactor());
        speedFineFactorSlowSetting.setDoubleValue(fineSettingsDTO.getSpeedFineFactorSlow());
        speedFineFactorFastSetting.setDoubleValue(fineSettingsDTO.getSpeedFineFactorFast());
        paymentDeadlineDaysSetting.setIntValue(fineSettingsDTO.getPaymentDeadlingDays());

        settingRepository.saveAll(List.of(emissionTimeFrameDaysSetting, emissionFineFactorSetting, speedFineFactorSlowSetting, speedFineFactorFastSetting, paymentDeadlineDaysSetting));
        return getFineSettingsDTO();
    }

    /**
     * Saves an updated ProcessorSettingsDTO to the repository
     *
     * @param processorSettingsDTO DTO with updated settings for the Processor
     * @return an updated ProcessorSettingsDTO object
     */
    public ProcessorSettingsDTO saveProcessorSettingsDTO(ProcessorSettingsDTO processorSettingsDTO) {
        IntSetting retriesSetting = getIntSetting("retries").get();
        BoolSetting loggingEnabledSetting = getBoolSetting("logFailed").get();
        StringSetting logPathSetting = getStringSetting("logPath").get();

        retriesSetting.setIntValue(processorSettingsDTO.getRetries());
        loggingEnabledSetting.setBoolValue(processorSettingsDTO.isLogFailed());
        logPathSetting.setStringValue(processorSettingsDTO.getLogPath());

        settingRepository.saveAll(List.of(retriesSetting, loggingEnabledSetting, logPathSetting));
        return getProcessorSettingsDTO();
    }

    /**
     * Gets all Settings from the repository.
     *
     * @return a List of Setting objects
     */
    public List<Setting> getSettings() {
        return settingRepository.findAll();
    }

    /**
     * Persists or updates a Setting the to repository.
     *
     * @param setting setting that needs to be persisted or updated
     * @return a Setting object from the repository
     */
    public Setting save(Setting setting) {
        return settingRepository.save(setting);
    }

    /**
     * Persists or updates a List of Setting the to repository.
     *
     * @param settings list of settings that need to be persisted or updated
     * @return a List of Setting objects from the repository
     */
    public List<Setting> saveSettings(List<Setting> settings) {
        return settingRepository.saveAll(settings);
    }
}

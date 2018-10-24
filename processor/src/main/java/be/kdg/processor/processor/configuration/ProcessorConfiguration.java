package be.kdg.processor.processor.configuration;

import be.kdg.processor.processor.dom.*;
import be.kdg.processor.processor.services.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Configuration class for processor package
 *
 * @author C&eacute;dric Goffin
 */
@Configuration
public class ProcessorConfiguration {
    @Value("${processor.retries}")
    private int retries;
    @Value("${processor.failed.log.enabled}")
    private boolean logFailed;
    @Value("${processor.failed.log.path}")
    private String logPath;
    @Value("${fine.emission.timeframe_days}")
    private int emissionTimeFrameDays;
    @Value("${fine.emission.fineFactor}")
    private double emissionFineFactor;
    @Value("${fine.speed.fineFactor.slow}")
    private double speedFineFactorSlow;
    @Value("${fine.speed.fineFactor.fast}")
    private double speedFineFactorFast;
    @Value("${fine.paymentDeadlineDays}")
    private int paymentDeadlineDays;

    private final SettingService settingService;

    /**
     * Constructor for ProcessorConfiguration.
     *
     * @param settingService the service for the processor package. Manages all processor settings.
     */
    @Autowired
    public ProcessorConfiguration(SettingService settingService) {
        this.settingService = settingService;
    }

    /**
     * Initializes the default Settings from application.properties and stores them to an in-memory H2 database.
     */
    @PostConstruct
    public void initSettings() {
        List<Setting> settings = List.of(
                new IntSetting("retries", retries),
                new BoolSetting("logFailed", logFailed),
                new StringSetting("logPath", logPath),
                new IntSetting("emissionTimeFrameDays", emissionTimeFrameDays),
                new DoubleSetting("emissionFineFactor", emissionFineFactor),
                new DoubleSetting("speedFineFactorSlow", speedFineFactorSlow),
                new DoubleSetting("speedFineFactorFast", speedFineFactorFast),
                new IntSetting("paymentDeadlineDays", paymentDeadlineDays)
        );
        settings = settingService.saveSettings(settings);
        System.out.println(settings);
    }
}

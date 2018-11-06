package be.kdg.processor.processor;

import be.kdg.processor.camera.dom.CameraMessage;
import be.kdg.processor.fine.dom.Fine;
import be.kdg.processor.fine.services.FineDetectionService;
import be.kdg.processor.fine.services.FineService;
import be.kdg.processor.processor.dom.BoolSetting;
import be.kdg.processor.processor.dom.IntSetting;
import be.kdg.processor.processor.dom.StringSetting;
import be.kdg.processor.processor.services.SettingService;
import be.kdg.processor.statistic.dom.DateTimeStatistic;
import be.kdg.processor.statistic.services.StatisticService;
import be.kdg.processor.utils.CSVUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

/**
 * Class that holds a buffer with all CameraMessages reported by the EventConsumer, along with how many times a certain messages has been tried to be processed.
 *
 * @author C&eacute;dric Goffin
 * @see be.kdg.processor.camera.consumers.EventConsumer
 * @see FineService
 */
@Component
@EnableScheduling
public class Processor {
    private static final Logger LOGGER = Logger.getLogger(Processor.class.getName());
    private final FineService fineService;
    private final FineDetectionService fineDetectionService;
    private final StatisticService statisticService;
    private final SettingService settingService;

    private final Map<CameraMessage, Integer> messageMap;

    @Value("${processor.retries}")
    private int retries;
    @Value("${processor.failed.log.enabled}")
    private boolean logFailed;
    @Value("${processor.failed.log.path}")
    private String logPath;
    private boolean isRunning = true;
    private int amountLogged;
    private String logFilePath;

    /**
     * Constructor used by Spring framework to initialize this class as a bean
     *
     * @param fineService          the service for the Fine package
     * @param fineDetectionService the service that detects which messages should get fined
     * @param statisticService     the service that manages statistics for the processor.
     * @param settingService       the service for the processor package. Manages all processor settings.
     */
    @Autowired
    public Processor(FineService fineService, FineDetectionService fineDetectionService, StatisticService statisticService, SettingService settingService) {
        this.fineService = fineService;
        this.fineDetectionService = fineDetectionService;
        this.statisticService = statisticService;
        this.settingService = settingService;
        this.messageMap = Collections.synchronizedMap(new HashMap<>());

        statisticService.saveStatistic(new DateTimeStatistic("startupTimestamp", LocalDateTime.now()));
    }

    /**
     * Method that is scheduled by spring to run every # milliseconds (configurable in application.properties).
     * If a message fails to be processed # amount of times (configurable via application.properties), it gets logged to a CSV file to 'src/main/resources/logger/" (path can be customized via application.properties).
     */
    @Scheduled(fixedDelayString = "${processor.processDelay_millis}")
    public void CheckMessages() {
        if (!isRunning) return;
        Map<CameraMessage, Integer> buffer;

        // Synchronize messageMap so no new items get added while messages get copied to buffer
        synchronized (messageMap) {
            if (messageMap.isEmpty()) return;

            // Copy & empty messageMap
            buffer = new HashMap<>(messageMap);
            messageMap.clear();
        }
        // Set initial messageCount for statistics
        int messageCount = buffer.size();

        // Get current settings
        Optional<IntSetting> optionalRetries = settingService.getIntSetting("retries");
        optionalRetries.ifPresent(s -> retries = s.getIntValue());
        Optional<BoolSetting> optionalLogFailed = settingService.getBoolSetting("logFailed");
        optionalLogFailed.ifPresent(s -> logFailed = s.getBoolValue());
        if (logFailed) {
            Optional<StringSetting> optionalLogPath = settingService.getStringSetting("logPath");
            optionalLogPath.ifPresent(s -> logPath = s.getStringValue());
        }

        // Process messages
        Map.Entry<List<Fine>, List<CameraMessage>> processingResult = fineDetectionService.processMessages(new ArrayList<>(buffer.keySet()));

        // Save fine result
        fineService.saveFines(processingResult.getKey());

        // Log failed messages or put them back in buffer
        if (!processingResult.getValue().isEmpty())
            LOGGER.warning(String.format("Failed to process %d message%s", processingResult.getValue().size(), ((processingResult.getValue().size() == 1) ? "" : "s")));

        amountLogged = 0;
        processingResult.getValue().forEach(m -> {
            int times = buffer.get(m);
            if (times < retries) messageMap.put(m, buffer.get(m) + 1);
            else if (logFailed) {
                // Log failed messages to file
                LOGGER.warning(String.format("Logging message '%s' to csv log in directory ('%s') because it failed to process more than %d times!", m, logPath, retries));
                logFilePath = CSVUtils.writeMessage(m, logPath);
                amountLogged++;
            } else {
                LOGGER.warning(String.format("Logging message '%s' to console because it failed to process more than %d times!", m, retries));
            }
        });
        if (logFailed && amountLogged > 0)
            LOGGER.info(String.format("Written %d messages to log file: '%s'", amountLogged, logFilePath));

        // Update statistics
        statisticService.incrementIntStatistic("successfulMessages", messageCount - processingResult.getValue().size());
        if (amountLogged > 0) statisticService.incrementIntStatistic("failedMessages", amountLogged);
        statisticService.incrementIntStatistic("totalMessages", messageCount - processingResult.getValue().size() + amountLogged);
    }

    /**
     * Method that adds a new CameraMessage to the buffer.
     *
     * @param message is a CameraMessage
     */
    public void reportMessage(CameraMessage message) {
        messageMap.put(message, 1);
    }

    /**
     * Getter for isPaused
     *
     * @return the value of isPaused
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Toggles the isRunning boolean
     */
    public void toggleProcessor() {
        isRunning = !isRunning;
    }
}

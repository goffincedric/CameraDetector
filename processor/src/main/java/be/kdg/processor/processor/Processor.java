package be.kdg.processor.processor;

import be.kdg.processor.camera.services.CameraServiceAdapter;
import be.kdg.processor.fine.FineDetector;
import be.kdg.processor.fine.dom.Fine;
import be.kdg.processor.fine.services.FineService;
import be.kdg.processor.camera.dom.Camera;
import be.kdg.processor.camera.dom.CameraMessage;
import be.kdg.processor.licenseplate.services.LicenseplateServiceAdapter;
import be.kdg.processor.licenseplate.misc.CloudALPRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

/**
 * @author Cédric Goffin
 * 02/10/2018 13:51
 */

@Component
@EnableScheduling
public class Processor {
    private static final Logger LOGGER = Logger.getLogger(Processor.class.getName());
    private final FineService fineService;

//    private ConcurrentMap<CameraMessage, Integer> messageMap;
    private final Map<CameraMessage, Integer> messageMap;

    @Value("${processor.retries}")
    private int retries;

    @Autowired
    public Processor(FineService fineService) {
        this.fineService = fineService;
        this.messageMap = Collections.synchronizedMap(new HashMap<>());
    }

    @Scheduled(fixedRate = 10000)
    public void CheckMessages() {
        Map<CameraMessage, Integer> buffer;

        // Synchronize messageMap so no new items get added while messages get copied to buffer
        synchronized (messageMap) {
            if (messageMap.isEmpty()) return;

            // Copy & empty messageMap
            buffer = new HashMap<>(messageMap);
            messageMap.clear();
        }

        // Process messages
        List<CameraMessage> unprocessed = fineService.processFines(new ArrayList<>(buffer.keySet()));

        unprocessed.forEach(m -> {
            int times = buffer.get(m);
            if (times <= retries) messageMap.put(m, buffer.get(m)+1);
            else {
                LOGGER.warning("Logging message '" + m + "' to file because it failed to process more than " + retries + " times!");
                //TODO: Log failed messages to file
            }
        });
    }

    public void reportMessage(CameraMessage message) {
        messageMap.put(message, 0);
    }
}

package be.kdg.processor;

import be.kdg.processor.Services.FineDetection;
import be.kdg.processor.model.camera.Camera;
import be.kdg.processor.model.camera.CameraMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cédric Goffin
 * 02/10/2018 13:51
 */

@Component
@EnableScheduling
public class Processor {
    private final FineDetection fineDetection;

    private List<CameraMessage> messageList;

    @Autowired
    public Processor(FineDetection fineDetection) {
        messageList = new ArrayList<>();
        this.fineDetection = fineDetection;
    }

    @Scheduled(fixedRate = 10000)
    public void CheckMessage() {
        if (messageList.isEmpty()) return;


        List<CameraMessage> messages = new ArrayList<>(messageList);
        messageList.clear();

        messages.forEach(m -> {
            Camera camera = fineDetection.getCamera(m.getId());
            if (camera.getSegment() != null) {

            }
        });
    }

    public boolean reportMessage(CameraMessage message) {
        return messageList.add(message);
    }
}

package be.kdg.simulator.Simulation;

import be.kdg.simulator.generators.MessageGenerator;
import be.kdg.simulator.messenger.Messenger;
import be.kdg.simulator.model.CameraMessage;
import org.quartz.*;
import org.quartz.core.QuartzScheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author Cédric Goffin
 * 21/09/2018 13:45
 */

@Component
public class Simulator {

    @Autowired
    private Messenger messenger;
    @Autowired
    private MessageGenerator messageGenerator;

    public void sendMessage() {
        CameraMessage message = messageGenerator.generate();
        messenger.sendMessage(message);

        try {
            Thread.sleep(message.getDelay());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

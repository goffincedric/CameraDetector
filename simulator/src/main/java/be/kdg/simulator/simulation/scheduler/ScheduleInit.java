package be.kdg.simulator.simulation.scheduler;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.logging.Logger;

/**
 * @author Cédric Goffin
 * 06/10/2018 12:54
 */
@Component
public class ScheduleInit {
    private static final Logger LOGGER = Logger.getLogger(ScheduleInit.class.getName());

    private final Scheduler scheduler;

    @Autowired
    public ScheduleInit(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @PostConstruct
    public void initSchedule() {
        try {
            scheduler.start();
            LOGGER.info("Scheduler has been started");
        } catch (SchedulerException se) {
            LOGGER.severe("An error occurred whilst trying to start the scheduler!");
        }
    }
}

package be.kdg.simulator.simulation.scheduler;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.logging.Logger;

/**
 * Class that will initialize Quartz Scheduler for spring
 *
 * @author C&eacute;dric Goffin
 * @see ScheduleConfig
 * @see ScheduledJob
 */
@Component
public class ScheduleInit {
    private static final Logger LOGGER = Logger.getLogger(ScheduleInit.class.getName());

    private final Scheduler scheduler;

    /**
     * Constructor for ScheduleInit.
     *
     * @param scheduler a Spring-enabled Scheduler
     */
    @Autowired
    public ScheduleInit(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Will initialize Scheduler at start of application.
     */
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

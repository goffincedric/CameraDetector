package be.kdg.simulator.scheduler;

import be.kdg.simulator.Simulation.Simulator;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author Cédric Goffin
 * 05/10/2018 12:34
 */
@Component
public class SimulationSchedule {
    private final Scheduler scheduler;

    @Autowired
    public SimulationSchedule(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @PostConstruct
    public void scheduleMessengers() {
        try {
            scheduler.start();
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }
}

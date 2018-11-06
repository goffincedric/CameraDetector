package be.kdg.simulator.simulation.scheduler;

import be.kdg.simulator.simulation.Simulator;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * Job that Quartz Scheduler will use to run in its schedulers
 *
 * @author C&eacute;dric Goffin
 * @see ScheduleConfig
 * @see ScheduleInit
 */
@Component
@DisallowConcurrentExecution
public class ScheduledJob implements Job {
    /**
     * Job to execute.
     *
     * @param context context that contains information from the scheduler
     */
    @Override
    public void execute(JobExecutionContext context) {
        Simulator simulator = (Simulator) context.getMergedJobDataMap().get("simulator");
        simulator.sendMessage();
    }
}

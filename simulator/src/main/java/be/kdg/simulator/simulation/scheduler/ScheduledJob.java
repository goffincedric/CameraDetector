package be.kdg.simulator.simulation.scheduler;

import be.kdg.simulator.simulation.Simulator;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

/**
 * @author Cédric Goffin
 * 05/10/2018 14:11
 */
@Component
@DisallowConcurrentExecution
public class ScheduledJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        Simulator simulator = (Simulator) context.getMergedJobDataMap().get("simulator");
        simulator.sendMessage();
    }
}

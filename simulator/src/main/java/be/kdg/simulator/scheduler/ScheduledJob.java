package be.kdg.simulator.scheduler;

import be.kdg.simulator.Simulation.Simulator;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * @author Cédric Goffin
 * 05/10/2018 14:11
 */
@Component
public class ScheduledJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Simulator simulator = (Simulator) context.getMergedJobDataMap().get("simulator");
        simulator.sendMessage();
    }
}

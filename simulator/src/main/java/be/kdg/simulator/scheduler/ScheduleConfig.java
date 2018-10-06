package be.kdg.simulator.scheduler;

import be.kdg.simulator.Simulation.Simulator;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author Cédric Goffin
 * 05/10/2018 13:20
 */
@Configuration
public class ScheduleConfig {

    private static String[] normalSchedule;
    private static String[] peakSchedule;
    private static int normalFrequencyMillis;
    private static int peakFrequencyMillis;

    @Value("${messenger.schedule.normal}")
    public void setNormalSchedule(String[] normalSchedule) {
        this.normalSchedule = normalSchedule;
    }

    @Value("${messenger.schedule.peak}")
    public void setPeakSchedule(String[] peakSchedule) {
        this.peakSchedule = peakSchedule;
    }

    @Value("${messenger.frequency.normal_millis}")
    public void setNormalFrequencyMillis(int normalFrequencyMillis) {
        ScheduleConfig.normalFrequencyMillis = normalFrequencyMillis;
    }

    @Value("${messenger.frequency.peak_millis}")
    public void setPeakFrequencyMillis(int peakFrequencyMillis) {
        ScheduleConfig.peakFrequencyMillis = peakFrequencyMillis;
    }

    @Autowired
    private Simulator simulator;

    @Bean
    public Scheduler scheduler() {
        Scheduler scheduler = null;
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        // Schedule tasks using different schedules and intervals
        addSchedule(scheduler, normalSchedule, "normal", normalFrequencyMillis);
        addSchedule(scheduler, peakSchedule, "peak", peakFrequencyMillis);

        return scheduler;
    }

    public JobDetail jobDetail(String schedule, String group) {
        // define the job and tie it to ScheduledJob class
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("simulator", simulator);

        return newJob(ScheduledJob.class)
                .withIdentity("job_" + group + "_" + schedule, group)
                .setJobData(jobDataMap)
                .build();
    }

    public Trigger trigger(JobDetail job, String schedule, String group, int interval) {
        // Get start and end times from schedule
        Instant start = LocalDate.now().atTime(LocalTime.parse(schedule.split(";")[0])).atZone(ZoneId.of("Europe/Brussels")).toInstant();
        Instant stop = LocalDate.now().atTime(LocalTime.parse(schedule.split(";")[1])).atZone(ZoneId.of("Europe/Brussels")).toInstant();

        // Trigger the job to run now, and then repeat every 40 seconds
        return newTrigger()
                .withIdentity("trigger_" + group + "_" + schedule, group)
                .startAt(Date.from(start))
                .endAt(Date.from(stop))
                .withSchedule(simpleSchedule()
                        .withIntervalInMilliseconds(interval)
                        .repeatForever())
                .build();
    }

    private void addSchedule(Scheduler scheduler, String[] schedule, String group, int interval) {
        Arrays.asList(schedule).forEach(s -> {
            try {
                JobDetail job = jobDetail(s, group);

                Trigger trigger = trigger(job, s, group, interval);

                // Tell quartz to schedule the job using our trigger
                scheduler.scheduleJob(job, trigger);

            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        });
    }
}

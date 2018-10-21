package be.kdg.simulator.simulation.scheduler;

import be.kdg.simulator.simulation.Simulator;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.*;
import java.util.*;
import java.util.logging.Logger;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author Cédric Goffin
 * 05/10/2018 13:20
 */
@Configuration
public class ScheduleConfig {
    private static final Logger LOGGER = Logger.getLogger(ScheduleConfig.class.getName());

    private final Simulator simulator;

    private String[] normalSchedule;
    private String[] peakSchedule;
    private int normalFrequencyMillis;
    private int peakFrequencyMillis;
    private int concurrentThreads;
    private String timezone;

    @Value("${simulation.schedule.normal}")
    public void setNormalSchedule(String[] normalSchedule) {
        this.normalSchedule = normalSchedule;
    }
    @Value("${simulation.schedule.peak}")
    public void setPeakSchedule(String[] peakSchedule) {
        this.peakSchedule = peakSchedule;
    }

    @Value("${simulation.frequency.normal_millis}")
    public void setNormalFrequencyMillis(int normalFrequencyMillis) {
        this.normalFrequencyMillis = normalFrequencyMillis;
    }
    @Value("${simulation.frequency.peak_millis}")
    public void setPeakFrequencyMillis(int peakFrequencyMillis) {
        this.peakFrequencyMillis = peakFrequencyMillis;
    }

    @Value("${generator.concurrent_threads}")
    public void setConcurrentThreads(int concurrentThreads) {
        this.concurrentThreads = concurrentThreads;
    }

    @Value("${simulation.timezone}")
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    @Autowired
    public ScheduleConfig(Simulator simulator) {
        this.simulator = simulator;
    }

    @Bean
    @ConditionalOnProperty(value = "generator", havingValue = "random")
    public Scheduler randomScheduler() {
        Scheduler scheduler = null;

        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();

            for (int i = 0; i < concurrentThreads; i++) {
                // Schedule tasks using different schedules and intervals
                addSchedule(scheduler, normalSchedule, "normal_Thread" + i, normalFrequencyMillis);
                addSchedule(scheduler, peakSchedule, "peak_Thread" + i, peakFrequencyMillis);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        return scheduler;
    }

    @Bean
    @ConditionalOnProperty(value = "generator", havingValue = "image")
    public Scheduler imageScheduler() {
        Scheduler scheduler = null;

        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();

            // Schedule tasks using different schedules and intervals
            addSchedule(scheduler, new String[]{"00:00:00;00:00:00"}, "image", 5000);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        return scheduler;
    }

    @Bean
    @ConditionalOnProperty(value = "generator", havingValue = "file")
    public Scheduler fileScheduler() {
        Scheduler scheduler = null;

        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();

            // Schedule tasks using different schedules and intervals
            addSchedule(scheduler, new String[]{"00:00:00;00:00:00"}, "file", 1);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        return scheduler;
    }

    private void addSchedule(Scheduler scheduler, String[] schedule, String group, int interval) {
        Arrays.asList(schedule).forEach(s -> {
            try {
                // Create job to schedule
                JobDetail job = jobDetail(s, group);

                // Set up trigger to start job
                Trigger trigger = trigger(job, s, group, interval);

                // Tell quartz to schedule the job using our trigger
                scheduler.scheduleJob(job, trigger);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        });
    }

    private JobDetail jobDetail(String schedule, String group) {
        // define the job and tie it to ScheduledJob class
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("simulator", simulator);

        return newJob(ScheduledJob.class)
                .withIdentity("job_" + group + "_" + schedule, group)
                .setJobData(jobDataMap)
                .build();
    }

    private Trigger trigger(JobDetail job, String schedule, String group, int interval) {
        // Get start and end times from schedule
        ZonedDateTime start = LocalDate.now().atTime(LocalTime.parse(schedule.split(";")[0])).atZone(ZoneId.of(timezone));
        ZonedDateTime stop = LocalDate.now().atTime(LocalTime.parse(schedule.split(";")[1])).atZone(ZoneId.of(timezone));
        if (stop.isBefore(start) || stop.isEqual(start))
            stop = stop.plusDays(1);

        // Trigger the job to run now, and then repeat every 40 seconds
        return newTrigger()
                .forJob(job)
                .withIdentity("trigger_" + group + "_" + schedule, group)
                .startAt(Date.from(start.toInstant()))
                .endAt(Date.from(stop.toInstant()))
                .withSchedule(simpleSchedule()
                        .withIntervalInMilliseconds(interval)
                        .repeatForever())
                .build();
    }
}

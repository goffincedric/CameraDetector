package be.kdg.simulator.simulation.scheduler;

import be.kdg.simulator.simulation.Simulator;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Logger;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Configuration class for Quartz Scheduler.
 *
 * @author C&eacute;dric Goffin
 * @see ScheduledJob
 * @see ScheduleInit
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

    /**
     * Constructor for ScheduleConfig.
     *
     * @param simulator a simulator to use in the scheduled task
     */
    @Autowired
    public ScheduleConfig(Simulator simulator) {
        this.simulator = simulator;
    }

    /**
     * Method that sets up schedule for random generation. Enables spring to use this Scheduler as a bean.
     *
     * @return a configured Scheduler object
     */
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
            LOGGER.severe("Failed to set up schedule for random generation!");
        }

        return scheduler;
    }

    /**
     * Method that sets up schedule for image generation. Enables spring to use this Scheduler as a bean.
     *
     * @return a configured Scheduler object
     */
    @Bean
    @ConditionalOnProperty(value = "generator", havingValue = "image")
    public Scheduler imageScheduler() {
        Scheduler scheduler = null;

        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();

            // Schedule tasks using different schedules and intervals
            addSchedule(scheduler, new String[]{"00:00:00;00:00:00"}, "image", 5000);
        } catch (SchedulerException e) {
            LOGGER.severe("Failed to set up schedule for image generation!");
        }

        return scheduler;
    }

    /**
     * Method that sets up schedule for file generation. Enables spring to use this Scheduler as a bean.
     *
     * @return a configured Scheduler object
     */
    @Bean
    @ConditionalOnProperty(value = "generator", havingValue = "file")
    public Scheduler fileScheduler() {
        Scheduler scheduler = null;

        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();

            // Schedule tasks using different schedules and intervals
            addSchedule(scheduler, new String[]{"00:00:00;00:00:00"}, "file", 1);
        } catch (SchedulerException e) {
            LOGGER.severe("Failed to set up schedule for file generation!");
        }

        return scheduler;
    }

    /**
     * Configures default scheduler
     *
     * @param scheduler a default scheduler
     * @param schedule  a string containing the time blocks to schedule
     * @param group     a group id
     * @param interval  an interval in milliseconds
     */
    private void addSchedule(Scheduler scheduler, String[] schedule, String group, int interval) {
        Arrays.asList(schedule).forEach(s -> {
            try {
                // Create job to schedule
                JobDetail job = jobDetail(s, group);

                // Set up trigger to start job
                Trigger trigger = trigger(job, s, group, interval);

                // Tell quartz to schedule the job using the trigger
                scheduler.scheduleJob(job, trigger);
            } catch (SchedulerException e) {
                LOGGER.severe("Failed to schedule messenger!");
            }
        });
    }

    /**
     * Creates a new job for the scheduler
     *
     * @param schedule a scheduler for the created job
     * @param group    a group id
     * @return a new JobDetail that will run the job
     */
    private JobDetail jobDetail(String schedule, String group) {
        // Define the job and tie it to ScheduledJob class
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("simulator", simulator);

        return newJob(ScheduledJob.class)
                .withIdentity("job_" + group + "_" + schedule, group)
                .setJobData(jobDataMap)
                .build();
    }

    /**
     * Creates a trigger that the scheduler will use to trigger the job to run
     *
     * @param job      a job to trigger
     * @param schedule a schedule that will use the new trigger
     * @param group    a group id
     * @param interval an interval in milliseconds
     * @return a new Trigger that will trigger the job
     */
    private Trigger trigger(JobDetail job, String schedule, String group, int interval) {
        // Get start and end times from schedule
        ZonedDateTime start = LocalDate.now().atTime(LocalTime.parse(schedule.split(";")[0])).atZone(ZoneId.of(timezone));
        ZonedDateTime stop = LocalDate.now().atTime(LocalTime.parse(schedule.split(";")[1])).atZone(ZoneId.of(timezone));
        if (stop.isBefore(start) || stop.isEqual(start))
            stop = stop.plusDays(1);

        // Trigger the job to run now, and then repeat forever after each interval
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

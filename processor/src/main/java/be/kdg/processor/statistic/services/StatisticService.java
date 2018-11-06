package be.kdg.processor.statistic.services;

import be.kdg.processor.statistic.dom.DateTimeStatistic;
import be.kdg.processor.statistic.dom.IntStatistic;
import be.kdg.processor.statistic.dom.ProcessorStatistics;
import be.kdg.processor.statistic.dom.Statistic;
import be.kdg.processor.statistic.repository.StatisticRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for the statistic package. Manages Statistics for the Processor.
 *
 * @author C&eacute;dric Goffin
 */
@Service
@Transactional
public class StatisticService {
    private final StatisticRepo statisticRepo;

    /**
     * Constructor used by Spring framework to initialize the service as a bean
     *
     * @param statisticRepo the repository that has access to the H2 in-memory database
     */
    @Autowired
    public StatisticService(StatisticRepo statisticRepo) {
        this.statisticRepo = statisticRepo;
    }

    /**
     * Gets information about a statistic.
     *
     * @param statisticName string with the unique name of the statistic
     * @return an Optional Statistic. Will be empty when no statistic with the supplied name was found.
     */
    public Optional<Statistic> getStatistic(String statisticName) {
        return statisticRepo.findByStatisticName(statisticName);
    }

    /**
     * Gets information about a statistic with an Integer value.
     *
     * @param statisticName string with the unique name of the statistic
     * @return an Optional IntStatistic. Will be empty when no statistic with the supplied name was found.
     */
    public Optional<IntStatistic> getIntStatistic(String statisticName) {
        Optional<Statistic> optionalSetting = getStatistic(statisticName);
        if (optionalSetting.isPresent() && optionalSetting.get() instanceof IntStatistic)
            return Optional.of((IntStatistic) optionalSetting.get());
        return Optional.empty();
    }

    /**
     * Gets information about a statistic with an LocalDateTime value.
     *
     * @param statisticName string with the unique name of the statistic
     * @return an Optional IntStatistic. Will be empty when no statistic with the supplied name was found.
     */
    public Optional<DateTimeStatistic> getDateTimeStatistic(String statisticName) {
        Optional<Statistic> optionalSetting = getStatistic(statisticName);
        if (optionalSetting.isPresent() && optionalSetting.get() instanceof DateTimeStatistic)
            return Optional.of((DateTimeStatistic) optionalSetting.get());
        return Optional.empty();
    }

    /**
     * Gets a ProcessorStatistics object with the current statistics from the Processor.
     *
     * @return a ProcessorStatistics object with the current statistics from the repository
     */
    public ProcessorStatistics getProcessorStatistics() {
        Optional<IntStatistic> optionalSuccessfulMessages = getIntStatistic("successfulMessages");
        Optional<IntStatistic> optionalFailedMessages = getIntStatistic("failedMessages");
        Optional<IntStatistic> optionalTotalMessages = getIntStatistic("totalMessages");
        Optional<DateTimeStatistic> optionalStartupTimestamp = getDateTimeStatistic("startupTimestamp");
        return new ProcessorStatistics(
                (optionalSuccessfulMessages.isPresent()) ? optionalSuccessfulMessages.get().getIntValue() : 0,
                (optionalFailedMessages.isPresent()) ? optionalFailedMessages.get().getIntValue() : 0,
                (optionalTotalMessages.isPresent()) ? optionalTotalMessages.get().getIntValue() : 0,
                (optionalStartupTimestamp.isPresent()) ? optionalStartupTimestamp.get().getDateTimeValue() : LocalDateTime.now()
        );
    }

    /**
     * Increments/saves an IntStatistic object from the repository.
     *
     * @param name  of the IntStatistic to be incremented
     * @param value amount the IntStatistic needs to be incremented with
     */
    public void incrementIntStatistic(String name, int value) {
        Optional<IntStatistic> optionalSuccessfulMessages = getIntStatistic(name);
        if (optionalSuccessfulMessages.isPresent()) {
            IntStatistic successfulMessages = optionalSuccessfulMessages.get();
            successfulMessages.setIntValue(successfulMessages.getIntValue() + value);
            saveStatistic(successfulMessages);
        } else {
            saveStatistic(new IntStatistic(name, value));
        }
    }

    /**
     * Updates/saves a DateTimeStatistic object with a given value.
     *
     * @param name  string with the name of the DateTimeStatistic
     * @param value new localDateTime value for the DateTimeStatistic object
     */
    public void saveDateTimeStatistic(String name, LocalDateTime value) {
        Optional<DateTimeStatistic> optionalDateTimeStatistic = getDateTimeStatistic(name);
        if (optionalDateTimeStatistic.isPresent()) {
            DateTimeStatistic dateTimeStatistic = optionalDateTimeStatistic.get();
            dateTimeStatistic.setDateTimeValue(value);
            saveStatistic(dateTimeStatistic);
        } else {
            saveStatistic(new DateTimeStatistic(name, value));
        }
    }

    /**
     * Persists or updates a Statistic the to repository.
     *
     * @param statistic Statistic that need to be persisted or updated
     */
    public void saveStatistic(Statistic statistic) {
        statisticRepo.save(statistic);
    }
}

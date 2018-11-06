package be.kdg.processor.statistic.dom;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;


/**
 * Statistic class that contains a LocalDateTime value
 *
 * @author Cédric Goffin
 */
@NoArgsConstructor
@Entity
public class DateTimeStatistic extends Statistic{
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "statisticId")
    private Statistic statistic;

    public DateTimeStatistic(String statisticName, LocalDateTime value) {
        super(statisticName, value.toString());
    }

    /**
     * Gets value as an LocalDateTime
     *
     * @return value as an LocalDateTime
     */
    public LocalDateTime getDateTimeValue() {
        return LocalDateTime.parse(super.getValue());
    }

    /**
     * Sets LocalDateTime as value
     *
     * @param value LocalDateTime value
     */
    public void setDateTimeValue(LocalDateTime value) {
        super.setValue(String.valueOf(value));
    }
}

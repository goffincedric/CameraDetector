package be.kdg.processor.statistic.dom;

import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Model class that holds information about processor statistics.
 *
 * @author Cédric Goffin
 * @see IntStatistic
 * @see DateTimeStatistic
 */
@NoArgsConstructor
@Entity
@Table(name = "tblStatistics")
public abstract class Statistic {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int statisticId;
    private String statisticName;
    private String value;

    public Statistic(String statisticName, String value) {
        this.statisticName = statisticName;
        this.value = value;
    }

    protected String getValue() {
        return value;
    }

    protected void setValue(String value) {
        this.value = value;
    }

}

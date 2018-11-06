package be.kdg.processor.statistic.dom;

import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Statistic class that contains an Integer value
 *
 * @author Cédric Goffin
 * @see Statistic
 */
@NoArgsConstructor
@Entity
public class IntStatistic extends Statistic {
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "statisticId")
    private Statistic statistic;

    public IntStatistic(String statisticName, Integer value) {
        super(statisticName, String.valueOf(value));
    }

    /**
     * Gets value as an Integer
     *
     * @return value as an Integer
     */
    public Integer getIntValue() {
        return Integer.parseInt(super.getValue());
    }

    /**
     * Sets Integer as value
     *
     * @param value Integer value
     */
    public void setIntValue(Integer value) {
        super.setValue(String.valueOf(value));
    }
}

package be.kdg.processor.processor.dom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Setting class that contains a Double value
 *
 * @author C&eacute;dric Goffin
 * @see be.kdg.processor.processor.dom.Setting
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
public class DoubleSetting extends Setting {
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "settingId")
    private Setting setting;

    public DoubleSetting(String settingName, Double value) {
        super(settingName, String.valueOf(value));
    }

    /**
     * Gets value as a Double
     *
     * @return value as a Double
     */
    public Double getDoubleValue() {
        return Double.parseDouble(super.getValue());
    }

    /**
     * Sets Double as value
     *
     * @param value Double value
     */
    public void setDoubleValue(Double value) {
        super.setValue(String.valueOf(value));
    }
}

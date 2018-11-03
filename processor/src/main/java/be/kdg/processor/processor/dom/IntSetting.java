package be.kdg.processor.processor.dom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Setting class that contains an Integer value
 *
 * @author C&eacute;dric Goffin
 * @see be.kdg.processor.processor.dom.Setting
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
public class IntSetting extends Setting {
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "settingId")
    private Setting setting;

    public IntSetting(String settingName, Integer value) {
        super(settingName, String.valueOf(value));
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

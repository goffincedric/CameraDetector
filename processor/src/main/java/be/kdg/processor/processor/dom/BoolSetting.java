package be.kdg.processor.processor.dom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Setting class that contains a Boolean value
 *
 * @author C&eacute;dric Goffin
 * @see be.kdg.processor.processor.dom.Setting
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BoolSetting extends Setting {
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "settingId")
    private Setting setting;

    public BoolSetting(String settingName, Boolean value) {
        super(settingName, String.valueOf(value));
    }

    /**
     * Gets value as a Boolean
     *
     * @return value as a Boolean
     */
    public Boolean getBoolValue() {
        return Boolean.parseBoolean(super.getValue());
    }

    /**
     * Sets Boolean as value
     *
     * @param value Boolean value
     */
    public void setBoolValue(Boolean value) {
        super.setValue(String.valueOf(value));
    }
}

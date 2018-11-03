package be.kdg.processor.processor.dom;

import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Setting class that contains a String value
 *
 * @author C&eacute;dric Goffin
 * @see be.kdg.processor.processor.dom.Setting
 */
@NoArgsConstructor
@Entity
public class StringSetting extends Setting {
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "settingId")
    private Setting setting;

    public StringSetting(String settingName, String value) {
        super(settingName, value);
    }

    /**
     * Gets value as a String
     *
     * @return value as a String
     */
    public String getStringValue() {
        return super.getValue();
    }

    /**
     * Sets String as value
     *
     * @param value String value
     */
    public void setStringValue(String value) {
        super.setValue(value);
    }
}

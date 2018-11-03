package be.kdg.processor.processor.dom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Model class that holds information about the current processor settings. Will take initial settings from application.properties.
 *
 * @author C&eacute;dric Goffin
 * @see BoolSetting
 * @see DoubleSetting
 * @see IntSetting
 * @see StringSetting
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "tblSettings")
public abstract class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int settingId;
    private String settingName;
    private String value;

    public Setting(String settingName, String value) {
        this.settingName = settingName;
        this.value = value;
    }

    protected String getValue() {
        return value;
    }

    protected void setValue(String value) {
        this.value = value;
    }
}

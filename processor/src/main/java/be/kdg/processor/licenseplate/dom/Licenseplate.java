package be.kdg.processor.licenseplate.dom;

import be.kdg.processor.fine.dom.Fine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class that holds information about a LicensePlate. Gets stored in an H2 in-memory database in a table named 'tblLicenseplate'.
 *
 * @author Cédric Goffin
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tblLicenseplate")
public class Licenseplate {
    @Id
    private String plateId;
    private String nationalNumber;
    private int euroNumber;
    @OneToMany(targetEntity = Fine.class, fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private List<Fine> fines = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Licenseplate that = (Licenseplate) o;

        if (plateId != null ? !plateId.equals(that.plateId) : that.plateId != null) return false;
        return nationalNumber != null ? nationalNumber.equals(that.nationalNumber) : that.nationalNumber == null;
    }

    @Override
    public int hashCode() {
        int result = plateId != null ? plateId.hashCode() : 0;
        result = 31 * result + (nationalNumber != null ? nationalNumber.hashCode() : 0);
        return result;
    }
}

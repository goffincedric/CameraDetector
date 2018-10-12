package be.kdg.processor.licenseplate.dom;

import be.kdg.processor.fine.dom.Fine;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Cédric Goffin
 * 01/10/2018 16:54
 */

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tblLicenseplate")
public class Licenseplate {
    @Id
    private String plateId;
    private String nationalNumber;
    private int euroNumber;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Fine> fines;

    public Licenseplate(String plateId, String nationalNumber, int euroNumber) {
        this.plateId = plateId;
        this.nationalNumber = nationalNumber;
        this.euroNumber = euroNumber;
        fines = new ArrayList<>();
    }
}

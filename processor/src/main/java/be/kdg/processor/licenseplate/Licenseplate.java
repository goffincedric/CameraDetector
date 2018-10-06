package be.kdg.processor.licenseplate;

import be.kdg.processor.fine.Fine;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cédric Goffin
 * 01/10/2018 16:54
 */

@Getter
@Setter(AccessLevel.NONE)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Licenseplate {
    private String plateId;
    private String nationalNumber;
    private int euroNumber;
    private List<Fine> fines;

    public Licenseplate(String plateId, String nationalNumber, int euroNumber) {
        this.plateId = plateId;
        this.nationalNumber = nationalNumber;
        this.euroNumber = euroNumber;
        fines = new ArrayList<>();
    }
}

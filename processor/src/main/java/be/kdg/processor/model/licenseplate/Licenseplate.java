package be.kdg.processor.model.licenseplate;

import lombok.*;

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
    private int plateId;
    private String nationalNumber;
    private int euroNumber;
}

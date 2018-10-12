package be.kdg.processor.fine.dom;

import lombok.*;

/**
 * @author Cédric Goffin
 * 01/10/2018 15:00
 */
@Getter
@Setter(AccessLevel.NONE)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class EmissionFine extends Fine {
    private int actualEmission;
    private int allowedEmission;

    @Override
    public String toString() {
        return "Amount: " + super.getAmount() + "; Emission: " + actualEmission + "; Allowed emission: " + allowedEmission;
    }
}

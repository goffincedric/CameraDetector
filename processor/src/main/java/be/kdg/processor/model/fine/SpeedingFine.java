package be.kdg.processor.model.fine;

import lombok.*;

/**
 * @author Cédric Goffin
 * 01/10/2018 14:59
 */
@Getter
@Setter(AccessLevel.NONE)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SpeedingFine extends Fine {
    private int actualSpeed;
    private int allowedSpeed;

    @Override
    public String toString() {
        return "Amount: " + super.getAmount() + "; Speed: " + actualSpeed + "; Speedlimit: " + allowedSpeed;
    }
}

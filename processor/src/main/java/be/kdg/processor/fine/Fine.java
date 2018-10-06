package be.kdg.processor.fine;

import be.kdg.processor.licenseplate.Licenseplate;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author Cédric Goffin
 * 01/10/2018 14:59
 */

@Getter
@Setter(AccessLevel.NONE)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public abstract class Fine {
    private double amount;
    private LocalDateTime timestamp;
    private LocalDateTime paymentDeadline;
    private Licenseplate licenseplate;
}

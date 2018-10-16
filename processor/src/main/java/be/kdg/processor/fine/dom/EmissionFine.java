package be.kdg.processor.fine.dom;

import be.kdg.processor.licenseplate.dom.Licenseplate;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Cédric Goffin
 * 01/10/2018 15:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EmissionFine extends Fine {
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="fineId")
    public Fine fine;
    private int actualEmission;
    private int allowedEmission;

    public EmissionFine(double amount, LocalDateTime timestamp, LocalDateTime paymentDeadline, Licenseplate licenseplate, int actualEmission, int allowedEmission) {
        super(amount, timestamp, paymentDeadline, licenseplate);
        this.actualEmission = actualEmission;
        this.allowedEmission = allowedEmission;
    }

    @Override
    public String toString() {
        return "Amount: " + super.getAmount() + "; Emission: " + actualEmission + "; Allowed emission: " + allowedEmission;
    }
}

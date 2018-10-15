package be.kdg.processor.fine.dom;

import be.kdg.processor.licenseplate.dom.Licenseplate;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Cédric Goffin
 * 01/10/2018 14:59
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SpeedingFine extends Fine {
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="fineId")
    public Fine fine;
    private int actualSpeed;
    private int allowedSpeed;

    public SpeedingFine(double amount, LocalDateTime timestamp, LocalDateTime paymentDeadline, Licenseplate licenseplate, int actualSpeed, int allowedSpeed) {
        super(amount, timestamp, paymentDeadline, licenseplate);
        this.actualSpeed = actualSpeed;
        this.allowedSpeed = allowedSpeed;
    }

    @Override
    public String toString() {
        return "Amount: " + super.getAmount() + "; Speed: " + actualSpeed + "; Speedlimit: " + allowedSpeed;
    }
}

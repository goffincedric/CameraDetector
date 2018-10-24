package be.kdg.processor.fine.dom;

import be.kdg.processor.licenseplate.dom.Licenseplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Model class that holds information about a SpeedingFine. Gets stored in an H2 in-memory database in a table named 'tblFine'.
 *
 * @author C&eacute;dric Goffin
 * @see Fine
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SpeedingFine extends Fine {
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "fineId")
    public Fine fine;
    private int actualSpeed;
    private int allowedSpeed;

    /**
     * EmissionFine constructor
     *
     * @param amount          is amount of the fine
     * @param timestamp       is the timestamp the fine was made
     * @param paymentDeadline is the deadline for when the fine must be paid
     * @param licenseplate    is the licenseplate of the owner that got fined
     * @param actualSpeed     is the actual speed of the car that passed the camera pair's segment
     * @param allowedSpeed    is the maximum allowed speed of the camera pair's segment
     */
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

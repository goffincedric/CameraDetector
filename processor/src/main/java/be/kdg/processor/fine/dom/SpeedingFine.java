package be.kdg.processor.fine.dom;

import be.kdg.processor.camera.dom.Camera;
import be.kdg.processor.licenseplate.dom.Licenseplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "fineId")
    public Fine fine;
    private int actualSpeed;
    private int allowedSpeed;

    /**
     * EmissionFine constructor
     *
     * @param amount          amount of the fine
     * @param timestamp       the timestamp the fine was made
     * @param paymentDeadline the deadline for when the fine must be paid
     * @param licenseplate    the licenseplate of the owner that got fined
     * @param actualSpeed     the actual speed of the car that passed the camera pair's segment
     * @param allowedSpeed    the maximum allowed speed of the camera pair's segment
     * @param cameras         the pair of cameras that recorded the cameramessages for this Fine
     */
    public SpeedingFine(double amount, LocalDateTime timestamp, LocalDateTime paymentDeadline, Licenseplate licenseplate, int actualSpeed, int allowedSpeed, List<Camera> cameras) {
        super(amount, timestamp, paymentDeadline, licenseplate, cameras);
        this.actualSpeed = actualSpeed;
        this.allowedSpeed = allowedSpeed;
    }

    @Override
    public String toString() {
        return "Amount: " + super.getAmount() + "; Speed: " + actualSpeed + "; Speedlimit: " + allowedSpeed;
    }
}

package be.kdg.processor.fine.dom;

import be.kdg.processor.camera.dom.Camera;
import be.kdg.processor.licenseplate.dom.Licenseplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Model class that holds information about an EmissionFine. Gets stored in an H2 in-memory database in a table named 'tblFine'.
 *
 * @author C&eacute;dric Goffin
 * @see Fine
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EmissionFine extends Fine {
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "fineId")
    public Fine fine;
    private int actualEmission;
    private int allowedEmission;

    /**
     * EmissionFine constructor
     *
     * @param amount          amount of the fine
     * @param timestamp       the timestamp the fine was made
     * @param paymentDeadline the deadline for when the fine must be paid
     * @param licenseplate    the licenseplate of the owner that got fined
     * @param actualEmission  the actual emission of the car of the license plate
     * @param allowedEmission the minumum emission euronorm of the camera that measured the actual emission
     * @param camera          the camera that recorded the cameramessage for this Fine
     */
    public EmissionFine(double amount, LocalDateTime timestamp, LocalDateTime paymentDeadline, Licenseplate licenseplate, int actualEmission, int allowedEmission, Camera camera) {
        super(amount, timestamp, paymentDeadline, licenseplate, camera);
        this.actualEmission = actualEmission;
        this.allowedEmission = allowedEmission;
    }

    @Override
    public String toString() {
        return "Amount: " + super.getAmount() + "; Emission: " + actualEmission + "; Allowed emission: " + allowedEmission;
    }
}

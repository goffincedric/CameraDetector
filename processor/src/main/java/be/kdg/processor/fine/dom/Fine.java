package be.kdg.processor.fine.dom;

import be.kdg.processor.licenseplate.dom.Licenseplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Abstract model class that holds information about a Fine. Gets stored in an H2 in-memory database in a table named 'tblFine'.
 *
 * @author C&eacute;dric Goffin
 * @see EmissionFine
 * @see SpeedingFine
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tblFine")
public abstract class Fine {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int fineId;
    private double amount;
    private LocalDateTime timestamp;
    private LocalDateTime paymentDeadline;
    @ManyToOne(targetEntity = Licenseplate.class, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private transient Licenseplate licenseplate;
    private String licenseplateId;
    private boolean isAccepted = false;
    private String motivation;

    /**
     * Fine constructor used by classes extending from Fine.
     *
     * @param amount          is amount of the fine
     * @param timestamp       is the timestamp the fine was made
     * @param paymentDeadline is the deadline for when the fine must be paid
     * @param licenseplate    is the licenseplate of the owner that got fined
     */
    public Fine(double amount, LocalDateTime timestamp, LocalDateTime paymentDeadline, Licenseplate licenseplate) {
        this.amount = amount;
        this.timestamp = timestamp;
        this.paymentDeadline = paymentDeadline;
        this.licenseplate = licenseplate;
        licenseplateId = licenseplate.getPlateId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Fine fine = (Fine) o;

        if (!timestamp.toLocalDate().equals(fine.timestamp.toLocalDate())) return false;
        return licenseplate.equals(fine.licenseplate);
    }

    @Override
    public int hashCode() {
        int result = timestamp.toLocalDate().hashCode();
        result = 31 * result + licenseplate.hashCode();
        return result;
    }
}

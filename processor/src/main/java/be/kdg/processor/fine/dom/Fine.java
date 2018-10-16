package be.kdg.processor.fine.dom;

import be.kdg.processor.licenseplate.dom.Licenseplate;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Cédric Goffin
 * 01/10/2018 14:59
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
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

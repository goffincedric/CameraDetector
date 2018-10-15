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
    @OneToOne(targetEntity = Licenseplate.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Licenseplate licenseplate;

    public Fine(double amount, LocalDateTime timestamp, LocalDateTime paymentDeadline, Licenseplate licenseplate) {
        this.amount = amount;
        this.timestamp = timestamp;
        this.paymentDeadline = paymentDeadline;
        this.licenseplate = licenseplate;
    }
}

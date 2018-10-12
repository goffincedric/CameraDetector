package be.kdg.processor.fine.dom;

import lombok.*;

import javax.persistence.*;

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

    @Override
    public String toString() {
        return "Amount: " + super.getAmount() + "; Speed: " + actualSpeed + "; Speedlimit: " + allowedSpeed;
    }
}

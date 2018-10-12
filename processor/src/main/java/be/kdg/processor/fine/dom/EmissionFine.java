package be.kdg.processor.fine.dom;

import lombok.*;

import javax.persistence.*;

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

    @Override
    public String toString() {
        return "Amount: " + super.getAmount() + "; Emission: " + actualEmission + "; Allowed emission: " + allowedEmission;
    }
}

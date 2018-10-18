package be.kdg.processor.fine.dto.fineDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Cédric Goffin
 * 16/10/2018 16:38
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmissionFineDTO extends FineDTO {
    private int actualEmission;
    private int allowedEmission;
}

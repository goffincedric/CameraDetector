package be.kdg.processor.fine.dto.fineDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A DTO for the Fine class, used to return information about an EmissionFine object.
 *
 * @author Cédric Goffin
 * @see be.kdg.processor.fine.dom.EmissionFine
 * @see FineDTO
 * @see be.kdg.processor.fine.controllers.rest.FineRestController
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmissionFineDTO extends FineDTO {
    private int actualEmission;
    private int allowedEmission;
}

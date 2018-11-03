package be.kdg.processor.fine.dto.fine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A DTO for the SpeedingFine class, used to return information about an SpeedingFine object.
 *
 * @author C&eacute;dric Goffin
 * @see be.kdg.processor.fine.dom.SpeedingFine
 * @see FineDTO
 * @see be.kdg.processor.fine.controllers.rest.FineRestController
 * 16/10/2018 16:39
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpeedingFineDTO extends FineDTO {
    private int actualSpeed;
    private int allowedSpeed;
}

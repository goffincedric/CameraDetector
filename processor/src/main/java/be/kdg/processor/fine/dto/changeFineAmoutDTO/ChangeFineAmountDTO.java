package be.kdg.processor.fine.dto.changeFineAmoutDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A DTO for the Fine class, used to return information about a Fine object whose amount / motivation got changed.
 *
 * @author Cédric Goffin
 * @see be.kdg.processor.fine.dom.Fine
 * @see be.kdg.processor.fine.controllers.rest.FineRestController
 */
@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeFineAmountDTO {
    private double amount;
    private String motivation;
}

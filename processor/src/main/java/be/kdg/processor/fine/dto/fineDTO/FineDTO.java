package be.kdg.processor.fine.dto.fineDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * A DTO for the Fine class, used by DTO classes extending FineDTO to return information about a Fine object.
 *
 * @author C&eacute;dric Goffin
 * @see be.kdg.processor.fine.controllers.rest.FineRestController
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class FineDTO {
    private int fineId;
    private double amount;
    private LocalDateTime timestamp;
    private LocalDateTime paymentDeadline;
    private String licenseplateId;
    private boolean isAccepted;
}

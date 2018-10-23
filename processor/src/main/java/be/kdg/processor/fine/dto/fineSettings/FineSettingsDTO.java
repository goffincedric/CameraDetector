package be.kdg.processor.fine.dto.fineSettings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO containing the current factors used in the fine amount calculation.
 *
 * @author Cédric Goffin
 * @see be.kdg.processor.fine.controllers.web.FineWebController
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FineSettingsDTO {
    private int emissionTimeframeDays;
    private double emissionFineFactor;
    private double speedFineFactorSlow;
    private double speedFineFactorFast;
    private int paymentDeadlingDays;
}

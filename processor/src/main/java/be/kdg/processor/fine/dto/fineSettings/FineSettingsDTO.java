package be.kdg.processor.fine.dto.fineSettings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO containing the current factors used in the fine amount calculation.
 *
 * @author C&eacute;dric Goffin
 * @see be.kdg.processor.processor.dom.FineSettings
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

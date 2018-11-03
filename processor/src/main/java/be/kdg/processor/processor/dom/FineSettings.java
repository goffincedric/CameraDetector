package be.kdg.processor.processor.dom;

import be.kdg.processor.fine.dto.fineSettings.FineSettingsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A settings class containing the current factors used in the fine amount calculation.
 *
 * @author C&eacute;dric Goffin
 * @see be.kdg.processor.fine.services.FineDetectionService
 * @see FineSettingsDTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FineSettings {
    private int emissionTimeframeDays;
    private double emissionFineFactor;
    private double speedFineFactorSlow;
    private double speedFineFactorFast;
    private int paymentDeadlingDays;
}

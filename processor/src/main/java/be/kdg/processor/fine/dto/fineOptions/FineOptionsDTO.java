package be.kdg.processor.fine.dto.fineOptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

/**
 * A DTO containing the current factors used in the fine amount calculation.
 *
 * @author Cédric Goffin
 * @see be.kdg.processor.fine.controllers.web.FineWebController
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FineOptionsDTO {
    @Value("${fine.emission.fineFactor}")
    private double emissionFineFactor;
    @Value("${fine.speed.fineFactor.slow}")
    private double speedFineFactorSlow;
    @Value("${fine.speed.fineFactor.fast}")
    private double speedFineFactorFast;
}

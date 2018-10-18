package be.kdg.processor.fine.dto.fineOptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author Cédric Goffin
 * 18/10/2018 22:42
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

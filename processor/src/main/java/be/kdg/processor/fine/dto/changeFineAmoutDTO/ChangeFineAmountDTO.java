package be.kdg.processor.fine.dto.changeFineAmoutDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Cédric Goffin
 * 17/10/2018 09:48
 */
@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeFineAmountDTO {
    private double amount;
    private String motivation;
}

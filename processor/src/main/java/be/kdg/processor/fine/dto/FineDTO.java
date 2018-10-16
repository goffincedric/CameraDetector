package be.kdg.processor.fine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Cédric Goffin
 * 16/10/2018 16:41
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
}

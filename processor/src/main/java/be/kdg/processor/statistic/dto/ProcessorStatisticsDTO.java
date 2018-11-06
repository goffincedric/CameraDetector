package be.kdg.processor.statistic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO for the ProcessorStatistics class, used to manipulate statistics for the processor.
 *
 * @author Cédric Goffin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessorStatisticsDTO {
    private int successfulMessages;
    private int failedMessages;
    private int totalMessages;
    private String startupTimestamp;
}

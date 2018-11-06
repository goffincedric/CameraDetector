package be.kdg.processor.statistic.dom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * A statistics class used to manipulate statistics for the processor.
 *
 * @author Cédric Goffin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessorStatistics {
    private int successfulMessages;
    private int failedMessages;
    private int totalMessages;
    private LocalDateTime startupTimestamp;
}

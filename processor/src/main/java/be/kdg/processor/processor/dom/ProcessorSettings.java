package be.kdg.processor.processor.dom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A settings class used to manipulate settings for the processor.
 *
 * @author C&eacute;dric Goffin
 * @see be.kdg.processor.processor.Processor
 * @see be.kdg.processor.processor.dto.ProcessorSettingsDTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessorSettings {
    private int retries;
    private boolean logFailed;
    private String logPath;
}

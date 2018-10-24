package be.kdg.processor.processor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *A DTO for the Setting class, used to manipulate settings for the processor.
 *
 * @author C&eacute;dric Goffin
 * @see be.kdg.processor.processor.dom.Setting
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessorSettingsDTO {
    private int retries;
    private boolean logFailed;
    private String logPath;
}

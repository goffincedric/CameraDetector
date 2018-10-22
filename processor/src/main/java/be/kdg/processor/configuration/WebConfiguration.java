package be.kdg.processor.configuration;

import be.kdg.processor.fine.dto.fineOptions.FineOptionsDTO;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for
 *
 * @author Cédric Goffin
 */
@Configuration
public class WebConfiguration {
    /**
     * Enables ModelMapper to be used by spring as a bean.
     *
     * @return a ModelMapper object
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /**
     * Enables FineOptionsDTO to be used by spring as a bean.
     *
     * @return a FineOptionsDTO object
     */
    @Bean
    public FineOptionsDTO fineOptionsDTO() {
        return new FineOptionsDTO();
    }
}

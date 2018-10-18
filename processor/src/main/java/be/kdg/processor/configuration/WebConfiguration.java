package be.kdg.processor.configuration;

import be.kdg.processor.fine.dto.fineOptions.FineOptionsDTO;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Cédric Goffin
 * 16/10/2018 17:03
 */
@Configuration
public class WebConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public FineOptionsDTO fineOptionsDTO() {
        return new FineOptionsDTO();
    }
}

package be.kdg.processor.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for general web controllers
 *
 * @author C&eacute;dric Goffin
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
}

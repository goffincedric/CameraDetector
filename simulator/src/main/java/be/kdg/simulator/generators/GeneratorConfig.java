package be.kdg.simulator.generators;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name="load",havingValue = "Config" )
public class GeneratorConfig {

    @Bean
    public MessageGenerator fileGenerator(){
        return new FileGenerator();
    }
}

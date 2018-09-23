package be.kdg.simulator.generators;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class GeneratorConfig {

    @Bean
    @ConditionalOnProperty(value = "generator", havingValue = "file")
    public MessageGenerator fileGenerator(){
        return new FileGenerator("src/main/resources/messages.csv");
    }

    @Bean
    @ConditionalOnProperty(value = "generator", havingValue = "random")
    public MessageGenerator randomGenerator(){
        return new RandomMessageGenerator();
    }
}

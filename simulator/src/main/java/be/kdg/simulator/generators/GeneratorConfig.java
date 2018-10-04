package be.kdg.simulator.generators;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeneratorConfig {

    @Value("${file_path}")
    private String filePath;

    @Bean
    @ConditionalOnProperty(value = "generator", havingValue = "file")
    public MessageGenerator fileGenerator(){
        return new FileGenerator(filePath);
    }

    @Bean
    @ConditionalOnProperty(value = "generator", havingValue = "random")
    public MessageGenerator randomGenerator(){
        return new RandomMessageGenerator();
    }
}

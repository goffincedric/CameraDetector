package be.kdg.simulator.generators;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;

@Configuration
public class GeneratorConfig {
    private static final Logger LOGGER = Logger.getLogger(GeneratorConfig.class.getName());

    @Value("${file_path}")
    private String filePath;
    @Value("${images_path}")
    private String imagesPath;

    @Bean
    @ConditionalOnProperty(value = "generator", havingValue = "file")
    public MessageGenerator fileGenerator(){
        return new FileGenerator(filePath);
    }

    @Bean
    @ConditionalOnProperty(value = "generator", havingValue = "random")
    public MessageGenerator randomGenerator(){
        return new RandomGenerator();
    }

    @Bean
    @ConditionalOnProperty(value = "generator", havingValue = "image")
    public MessageGenerator imageGenerator(){
        MessageGenerator messageGenerator = null;
        try {
            messageGenerator = new ImageGenerator(imagesPath);
        } catch (IllegalArgumentException iae) {
            LOGGER.severe(iae.getMessage());
        }
        return messageGenerator;
    }
}

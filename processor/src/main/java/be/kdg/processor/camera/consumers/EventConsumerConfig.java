package be.kdg.processor.camera.consumers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for EventConsumer
 * @see be.kdg.processor.camera.consumers.EventConsumer
 * @author Cédric Goffin
 */
@Configuration
public class EventConsumerConfig {

    @Bean
    public EventConsumer eventReceiver() {
        return new EventConsumer();
    }
}

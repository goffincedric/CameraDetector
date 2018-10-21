package be.kdg.processor.camera.consumers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for EventConsumer
 *
 * @author Cédric Goffin
 * @see EventConsumer
 */
@Configuration
public class EventConsumerConfig {
    /**
     * Enables EventConsumer to be used by spring as a bean.
     *
     * @return an EventConsumer object
     */
    @Bean
    public EventConsumer eventReceiver() {
        return new EventConsumer();
    }
}

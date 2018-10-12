package be.kdg.processor.camera.consumers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Cédric Goffin
 * 28/09/2018 11:32
 */
@Configuration
public class EventConsumerConfig {

    @Bean
    public EventConsumer eventReceiver() {
        return new EventConsumer();
    }
}

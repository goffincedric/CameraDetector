package be.kdg.simulator.messenger;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for QueueMessenger.
 *
 * @author Cédric Goffin
 * @see QueueMessenger
 */
@Configuration
public class QueueConfig {
    @Value("${mqtt.queue_name}")
    private String QUEUE_NAME;

    /**
     * Enables Queue to be used by spring as a bean.
     *
     * @return a Queue object
     */
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME);
    }
}

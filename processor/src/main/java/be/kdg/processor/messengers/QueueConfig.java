package be.kdg.processor.messengers;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Cédric Goffin
 * 28/09/2018 11:32
 */
@Configuration
public class QueueConfig {
    @Value("${mqtt.queue_name}")
    private String QUEUE_NAME;

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME);
    }
}

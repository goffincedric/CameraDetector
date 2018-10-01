package be.kdg.processor;

import be.kdg.sa.services.CameraServiceProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Cédric Goffin
 * 01/10/2018 15:10
 */
@Configuration
public class CameraServiceConfig {
    @Bean
    public CameraServiceProxy cameraServiceProxy() {
        return new CameraServiceProxy();
    }
}

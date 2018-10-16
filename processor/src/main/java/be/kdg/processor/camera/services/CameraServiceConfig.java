package be.kdg.processor.camera.services;

import be.kdg.sa.services.CameraServiceProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Cédric Goffin
 * 14/10/2018 14:51
 */
@Configuration
public class CameraServiceConfig {
    @Bean
    public CameraServiceProxy cameraServiceProxy() {
        return new CameraServiceProxy();
    }
}

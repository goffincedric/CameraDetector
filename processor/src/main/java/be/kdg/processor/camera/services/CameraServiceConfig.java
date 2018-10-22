package be.kdg.processor.camera.services;

import be.kdg.sa.services.CameraServiceProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for CameraServiceProxy
 *
 * @author Cédric Goffin
 * @see CameraServiceProxy
 */
@Configuration
public class CameraServiceConfig {
    /**
     * Enables CameraServiceProxy to be used by spring as a bean.
     *
     * @return a CameraServiceProxy object
     */
    @Bean
    public CameraServiceProxy cameraServiceProxy() {
        return new CameraServiceProxy();
    }
}

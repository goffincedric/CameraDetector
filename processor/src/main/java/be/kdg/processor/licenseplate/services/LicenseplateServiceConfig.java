package be.kdg.processor.licenseplate.services;

import be.kdg.sa.services.LicensePlateServiceProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for LicensePlateServiceProxy
 *
 * @author C&eacute;dric Goffin
 * @see LicensePlateServiceProxy
 */
@Configuration
public class LicenseplateServiceConfig {
    /**
     * Enables LicensePlateServiceProxy to be used by spring as a bean.
     *
     * @return a LicensePlateServiceProxy object
     */
    @Bean
    public LicensePlateServiceProxy licensePlateServiceProxy() {
        return new LicensePlateServiceProxy();
    }
}

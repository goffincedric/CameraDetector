package be.kdg.processor.licenseplate.repository;

import be.kdg.processor.licenseplate.dom.Licenseplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Cédric Goffin
 * 16/10/2018 14:02
 */
@Repository
public interface LicenseplateRepository extends JpaRepository<Licenseplate, String> {
}

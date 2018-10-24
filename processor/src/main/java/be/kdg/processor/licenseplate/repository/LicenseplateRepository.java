package be.kdg.processor.licenseplate.repository;

import be.kdg.processor.licenseplate.dom.Licenseplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository that gets used to persist Fines to an H2 in-memory database.
 *
 * @author C&eacute;dric Goffin
 * @see Licenseplate
 */
@Repository
public interface LicenseplateRepository extends JpaRepository<Licenseplate, String> {
}

package be.kdg.processor.processor.repository;

import be.kdg.processor.processor.dom.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository that gets used to persist Settings to an H2 in-memory database.
 *
 * @author C&eacute;dric Goffin
 * @see Setting
 */
@Repository
public interface SettingRepository extends JpaRepository<Setting, Integer> {
    Optional<Setting> findBySettingName(String settingname);
}

package be.kdg.processor.statistic.repository;

import be.kdg.processor.statistic.dom.Statistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository that gets used to persist Statistics to an H2 in-memory database.
 *
 * @author C&eacute;dric Goffin
 * @see Statistic
 */
public interface StatisticRepo extends JpaRepository<Statistic, Integer> {
    Optional<Statistic> findByStatisticName(String settingname);
}

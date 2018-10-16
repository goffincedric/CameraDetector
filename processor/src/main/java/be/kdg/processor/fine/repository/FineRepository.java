package be.kdg.processor.fine.repository;

import be.kdg.processor.fine.dom.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Cédric Goffin
 * 12/10/2018 14:26
 */
@Repository
public interface FineRepository extends JpaRepository<Fine, Integer> {
    List<Fine> findAllByTimestampBetween(LocalDateTime from, LocalDateTime to);
}

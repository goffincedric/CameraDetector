package be.kdg.processor.fine.repository;

import be.kdg.processor.fine.dom.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository used to persist Fines to an H2 in-memory database.
 *
 * @author Cédric Goffin
 * @see Fine
 */
@Repository
public interface FineRepository extends JpaRepository<Fine, Integer> {
    List<Fine> findAllByTimestampBetween(LocalDateTime from, LocalDateTime to);
}

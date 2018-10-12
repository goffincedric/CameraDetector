package be.kdg.processor.fine.repository;

import be.kdg.processor.fine.dom.Fine;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Cédric Goffin
 * 12/10/2018 14:26
 */
public interface FineRepository extends JpaRepository<Fine, Integer> {

}

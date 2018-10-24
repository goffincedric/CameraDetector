package be.kdg.processor.user.repository;

import be.kdg.processor.user.dom.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository that gets used to persist Users to an H2 in-memory database.
 *
 * @author C&eacute;dric Goffin
 * @see User
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}

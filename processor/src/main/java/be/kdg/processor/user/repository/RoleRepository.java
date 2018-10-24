package be.kdg.processor.user.repository;

import be.kdg.processor.user.dom.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository that gets used to persist Roles to an H2 in-memory database.
 *
 * @author C&eacute;dric Goffin
 * @see Role
 */
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}

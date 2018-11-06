package be.kdg.processor.user.services;

import be.kdg.processor.user.dom.Role;
import be.kdg.processor.user.dom.User;
import be.kdg.processor.user.exceptions.UserException;
import be.kdg.processor.user.repository.RoleRepository;
import be.kdg.processor.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service used to manipulate Fine information from the H2 in-memory database.
 *
 * @author C&eacute;dric Goffin
 * @see UserRepository
 */
@Service
@Transactional
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Constructor for UserService.
     *
     * @param userRepository        is the repository that has access to the H2 in-memory database user table
     * @param roleRepository        is the repository that has access to the H2 in-memory database role table
     * @param bCryptPasswordEncoder is the encoder used to encode the user's password
     */
    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * Gets information about a User.
     *
     * @param username a string containing a username
     * @return an Optional User. Is empty when no User could be found or when an error occurred.
     * @throws UserException when no User with the supplied username could be found
     */
    public Optional<User> getUser(String username) throws UserException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            return optionalUser;
        }
        throw new UserException("User with username '" + username + "' does not exist!");
    }

    /**
     * Gets information about a User.
     *
     * @param name a string containing a role name
     * @return an Optional Role. Is empty when no Role could be found or when an error occurred.
     */
    public Optional<Role> getRole(String name) {
        return roleRepository.findByName(name);
    }

    /**
     * Method that registers a new user in the repository
     *
     * @param user the new user to register
     * @return the registered user from the repository
     * @throws UserException when a user with the same username already exists
     */
    public User save(User user) throws UserException {
        if (userRepository.findByUsername(user.getUsername()).isPresent())
            throw new UserException(String.format("User with username %s already exists", user.getUsername()));

        // User does not exist, create new user
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Creates multiple Roles in the repository.
     *
     * @param roles a list of Roles that will be persisted to the database
     * @return a list of Roles from the repository that were persisted to the database
     */
    public List<Role> saveRoles(List<Role> roles) {
        return roleRepository.saveAll(roles);
    }

    /**
     * Method that updates a user in the repository
     *
     * @param updatedUser user with updated information
     * @return the updated user from the repository
     * @throws UserException when no user with the supplied username could be found
     */
    public User updateUser(User updatedUser) throws UserException {
        Optional<User> optionalUser = getUser(updatedUser.getUsername());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            user.setPassword(bCryptPasswordEncoder.encode(updatedUser.getPassword()));
            user.setRoles(updatedUser.getRoles());

            return userRepository.save(user);
        }
        throw new UserException("User with username '" + updatedUser.getUsername() + "' does not exist!");
    }

    /**
     * Method that deletes a user from the repository
     *
     * @param username string with username of user that needs to be deleted
     * @throws UserException when no user with the supplied username could be found
     */
    public void deleteUser(String username) throws UserException {
        Optional<User> optionalUser = getUser(username);
        if (optionalUser.isPresent()) {
            userRepository.delete(optionalUser.get());
        } else {
            throw new UserException("User with username '" + username + "' does not exist!");
        }
    }

    /**
     * Gets information about a User. Used by AuthenticationManagerBuilder from spring security.
     *
     * @param username a string containing a username
     * @return a user
     * @throws UsernameNotFoundException when no user could be found with the supplied username
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    user.getRoles().stream()
                            .map(r -> new SimpleGrantedAuthority(r.getName()))
                            .collect(Collectors.toSet())
            );
        }
        throw new UsernameNotFoundException(String.format("No user found with username: %s", username));
    }
}

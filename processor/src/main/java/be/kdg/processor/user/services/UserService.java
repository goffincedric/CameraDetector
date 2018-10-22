package be.kdg.processor.user.services;

import be.kdg.processor.user.dom.User;
import be.kdg.processor.user.exceptions.UserException;
import be.kdg.processor.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service used to manipulate Fine information from the H2 in-memory database.
 *
 * @author Cédric Goffin
 * @see UserRepository
 */
@Service
@Transactional
public class UserService implements IUserService, UserDetailsService {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Constructor for UserService.
     *
     * @param userRepository        is the repository that has access to the H2 in-memory database
     * @param bCryptPasswordEncoder is the encoder used to encode the user's password
     */
    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * Method that registers a new user in the repository
     *
     * @param user the new user to register
     * @return the registered user from the repository
     * @throws UserException when a user with the same username already exists
     */
    @Override
    public User createUser(User user) throws UserException {
        if (getUser(user.getUsername()).isPresent())
            throw new UserException(String.format("User with username %s already exists", user.getUsername()));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Gets information about a User.
     *
     * @param username a string containing a username
     * @return an Optional User. Is empty when no User could be found or when an error occurred.
     */
    @Override
    public Optional<User> getUser(String username) {
        return userRepository.findAll().stream().filter(u -> u.getUsername().equals(username)).findFirst();
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
        Optional<User> optionalUser = userRepository.findAll().stream().filter(u -> u.getUsername().equals(username)).findFirst();
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            org.springframework.security.core.userdetails.User user2 = new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    user.getRoles().stream()
                            .map(r -> new SimpleGrantedAuthority(r.getName()))
                            .collect(Collectors.toSet())
            );
            System.out.println(user2.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("admin")));
            return user2;
        }
        throw new UsernameNotFoundException(String.format("No user found with username: %s", username));
    }


}

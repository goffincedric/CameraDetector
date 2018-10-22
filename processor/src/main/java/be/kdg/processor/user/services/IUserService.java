package be.kdg.processor.user.services;

import be.kdg.processor.user.dom.User;
import be.kdg.processor.user.exceptions.UserException;

import java.util.Optional;

/**
 * @author Cédric Goffin
 * 22/10/2018 16:58
 */
public interface IUserService {
    User createUser(User user) throws UserException;
    Optional<User> getUser(String username);
}

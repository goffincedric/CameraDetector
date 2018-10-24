package be.kdg.processor.user.controllers.rest;

import be.kdg.processor.user.dom.Role;
import be.kdg.processor.user.dom.User;
import be.kdg.processor.user.dto.SafeUserDTO;
import be.kdg.processor.user.dto.UserDTO;
import be.kdg.processor.user.exceptions.UserException;
import be.kdg.processor.user.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Rest controller for Camera package. Mapped to listen to requests on /api/user
 *
 * @author C&eacute;dric Goffin
 */
@RestController
@RequestMapping("/api/user")
public class UserRestController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    /**
     * Constructor for UserRestController.
     *
     * @param userService is the service for the User package
     * @param modelMapper is a mapper that most methods use to map an object to DTO and vice versa
     */
    @Autowired
    public UserRestController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;

        modelMapper.createTypeMap(UserDTO.class, User.class).setConverter(context -> {
            List<Role> roles = context.getSource().getRoles().stream()
                    .map(rs -> {
                        Optional<Role> optionalRole = userService.getRole(rs);
                        return optionalRole.orElseGet(() -> new Role(rs));
                    }).collect(Collectors.toList());
            return new User(context.getSource().getUsername(), context.getSource().getPassword(), roles);
        });
        modelMapper.createTypeMap(User.class, UserDTO.class).setConverter(context -> {
            List<String> roles = context.getSource().getRoles().stream()
                    .map(Role::getName).collect(Collectors.toList());
            return new UserDTO(context.getSource().getUsername(), context.getSource().getPassword(), roles);
        });
        modelMapper.createTypeMap(User.class, SafeUserDTO.class).setConverter(context -> {
            List<String> roles = context.getSource().getRoles().stream()
                    .map(Role::getName).collect(Collectors.toList());
            return new SafeUserDTO(context.getSource().getUsername(), roles);
        });
    }

    /**
     * Method that handles incoming GET requests from /api/user.
     *
     * @param username a string containing the username
     * @return a SafeUSerDTO with the information about the user (no password)
     * @throws UserException when a user with the supplied username could not be found
     */
    @GetMapping
    public ResponseEntity getUser(@RequestParam(value = "username") String username) throws UserException {
        return new ResponseEntity<>(
                modelMapper.map(userService.getUser(username).get(), SafeUserDTO.class),
                HttpStatus.OK
        );
    }

    /**
     * Method that handles incoming POST requests from /api/user.
     *
     * @param userDTO a DTO containing information about a User
     * @return a ResponseEntity with the status code
     * @throws UserException when a user with the same username already exists
     */
    @PostMapping
    public ResponseEntity addUser(@Valid @RequestBody UserDTO userDTO) throws UserException {
        User user = userService.save(modelMapper.map(userDTO, User.class));
        return new ResponseEntity<>(
                modelMapper.map(user, SafeUserDTO.class),
                HttpStatus.OK
        );
    }

    /**
     * Method that handles incoming PUT requests from /api/user.
     *
     * @param userDTO a DTO containing updated information about a User
     * @return a SafeUSerDTO with the updated information about the user (no password)
     * @throws UserException when a user with the supplied username could not be found
     */
    @PutMapping
    public ResponseEntity updateUser(@Valid @RequestBody UserDTO userDTO) throws UserException {
        User user = userService.updateUser(modelMapper.map(userDTO, User.class));
        return new ResponseEntity<>(
                modelMapper.map(user, SafeUserDTO.class),
                HttpStatus.OK
        );
    }

    /**
     * Method that handles incoming DELETE requests from /api/user.
     *
     * @param username a string containing the username of the User that needs to be deleted
     * @return a ResponseEntity with the status code
     * @throws UserException when a user with the supplied username could not be found
     */
    @DeleteMapping
    public ResponseEntity deleteUser(@RequestParam(value = "username") String username) throws UserException {
        userService.deleteUser(username);
        return new ResponseEntity<>(
                "deleted " + username,
                HttpStatus.OK
        );
    }
}

package be.kdg.processor.user.controllers.rest;

import be.kdg.processor.user.dom.User;
import be.kdg.processor.user.dto.UserDTO;
import be.kdg.processor.user.exceptions.UserException;
import be.kdg.processor.user.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Rest controller for Camera package. Mapped to listen to requests on /api/user
 *
 * @author Cédric Goffin
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
    }

    /**
     * Method that handles incoming GET requests from /api/user/register.
     *
     * @param userDTO a DTO containing information about a User
     * @return a DTO containing information about the new registered User
     * @throws UserException when a user with the same username already exists
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> addUser(@Valid @RequestBody UserDTO userDTO) throws UserException {
        User user = modelMapper.map(userDTO, User.class);
        user = userService.createUser(user);
        return new ResponseEntity<>(
                modelMapper.map(user, UserDTO.class),
                HttpStatus.OK
        );
    }

}

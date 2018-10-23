package be.kdg.processor.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A DTO for the User class, used to return information about a user.
 *
 * @author Cédric Goffin
 * @see be.kdg.processor.user.controllers.rest.UserRestController
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SafeUserDTO {
    private String username;
    private List<String> roles;
}

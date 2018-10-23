package be.kdg.processor.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * A DTO for the User class, used to manipulate information about a user.
 *
 * @author Cédric Goffin
 * @see be.kdg.processor.user.controllers.rest.UserRestController
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @NotNull
    @NotEmpty
    private String username;
    @NotNull
    @NotEmpty
    private String password;
    @NotNull
    @NotEmpty
    private List<String> roles;
}

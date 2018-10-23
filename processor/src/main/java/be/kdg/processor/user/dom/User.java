package be.kdg.processor.user.dom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * Model class that holds information about a user. Gets stored in an H2 in-memory database in a table named 'tblUser'.
 *
 * @author Cédric Goffin
 * @see Role
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tblUser")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int userId;
    private String username;
    private String password;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "tbl_user_role", joinColumns = @JoinColumn(name = "userId"), inverseJoinColumns = @JoinColumn(name = "roleId"))
    private List<Role> roles;

    /**
     * Constructor for User.
     *
     * @param username a string containing a username
     * @param password a string containing a password
     * @param roles    a list containing all the roles the user has
     */
    public User(String username, String password, List<Role> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }
}

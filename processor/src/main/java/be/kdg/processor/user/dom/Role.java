package be.kdg.processor.user.dom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class that holds information about a role. Gets stored in an H2 in-memory database in a table named 'tblRole'.
 *
 * @author Cédric Goffin
 * @see User
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tblRole")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int roleId;
    private String name;
    @ManyToMany(mappedBy = "roles")
    private List<User> users = new ArrayList<>();

    /**
     * Constructor for Role.
     *
     * @param name a string containing the role name
     */
    public Role(String name) {
        this.name = name;
    }
}

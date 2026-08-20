package quickshow.dbms.project.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Admin")
public class Admin {

    @Id
    @Column(name = "AdminID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer adminId;

    @Column(name = "FirstName", length = 50)
    private String firstName;

    @Column(name = "MiddleName", length = 50)
    private String middleName;

    @Column(name = "LastName", length = 50)
    private String lastName;

    @Column(name = "Role", length = 30)
    private String role;

    @Column(name = "Password", length = 255)
    private String password;

    @Column(name = "Email", length = 100)
    private String email;
}



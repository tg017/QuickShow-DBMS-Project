package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminRegisterRequestDTO {

    private String firstName;

    private String middleName;

    private String lastName;

    private String role;

    private String email;

    private String password;
}
package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginResponseDTO {

    private Integer adminId;

    private String firstName;

    private String lastName;

    private String email;

    private String role;

    private String token;

    private String message;
}
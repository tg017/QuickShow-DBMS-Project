package quickshow.dbms.project.repository.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomerLoginData {

    private Integer userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
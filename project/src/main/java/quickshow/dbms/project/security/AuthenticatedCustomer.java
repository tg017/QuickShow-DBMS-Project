package quickshow.dbms.project.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticatedCustomer {

    private Integer userId;

    private String email;
}
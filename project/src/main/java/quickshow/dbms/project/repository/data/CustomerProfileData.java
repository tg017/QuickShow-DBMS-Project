package quickshow.dbms.project.repository.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomerProfileData {

    private Integer userId;

    private String firstName;

    private String middleName;

    private String lastName;

    private String email;

    private String phoneNo;

    private String city;

    private String state;

    private String pinCode;
}
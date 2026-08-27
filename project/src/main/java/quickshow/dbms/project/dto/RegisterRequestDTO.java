package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

    private String firstName;

    private String middleName;

    private String lastName;

    private LocalDate dob;

    private String gender;

    private String phoneNo;

    private String password;

    private String houseNo;

    private String street;

    private String area;

    private String city;

    private String state;

    private String pinCode;

    private String email;
}
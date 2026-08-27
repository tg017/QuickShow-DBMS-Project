package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeResponseDTO {

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
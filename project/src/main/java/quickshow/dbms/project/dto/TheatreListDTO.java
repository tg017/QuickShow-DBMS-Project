package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TheatreListDTO {

    private Integer theatreId;
    private String name;
    private String buildingName;
    private String street;
    private String area;
    private String city;
    private String state;
    private String pinCode;
}
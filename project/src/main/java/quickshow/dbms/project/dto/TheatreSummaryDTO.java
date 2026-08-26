package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TheatreSummaryDTO {

    private Integer theatreId;
    private String name;
    private String city;
}
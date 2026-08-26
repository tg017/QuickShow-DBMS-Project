package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScreenSummaryDTO {

    private Integer screenId;
    private String name;
    private String screenType;
}
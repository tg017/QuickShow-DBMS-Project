package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import quickshow.dbms.project.model.ScreenType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminScreenDTO {

    private Integer screenId;

    private String name;

    private ScreenType screenType;

    private Integer capacity;

    private Integer theatreId;
}
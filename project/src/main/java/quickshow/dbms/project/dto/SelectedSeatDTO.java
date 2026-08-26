package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SelectedSeatDTO {

    private Integer seatId;
    private String rowNo;
    private Integer seatNo;
}
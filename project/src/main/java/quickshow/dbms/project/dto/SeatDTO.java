package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatDTO {

    private Integer seatId;
    private String rowNo;
    private Integer seatNo;
    private String status;
}
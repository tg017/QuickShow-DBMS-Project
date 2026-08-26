package quickshow.dbms.project.repository.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SelectedSeatData {

    private Integer seatId;
    private String rowNo;
    private Integer seatNo;
}
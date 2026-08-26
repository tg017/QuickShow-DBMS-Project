package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class ShowSummaryDTO {

    private Integer showId;
    private LocalDate showDate;
    private LocalTime showTime;
}
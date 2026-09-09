package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import quickshow.dbms.project.model.ShowStatus;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminShowDTO {
    private Integer showId;

    private LocalDate showDate;

    private LocalTime showTime;

    private Long ticketPrice;

    private Integer availableSeats;

    private ShowStatus showStatus;

    private Integer movieId;

    private Integer screenId;
}
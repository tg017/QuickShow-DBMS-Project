package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class SeatLayoutDTO {

    private Integer showId;

    private MovieSummaryDTO movie;

    private TheatreSummaryDTO theatre;

    private ScreenSummaryDTO screen;

    private LocalDate showDate;

    private LocalTime showTime;

    private Long ticketPrice;

    private Integer availableSeats;

    private String showStatus;

    private List<SeatDTO> seats;
}
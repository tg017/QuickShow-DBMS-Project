package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class TheatreShowDTO {

    private Integer showId;
    private String movieTitle;
    private String language;
    private String screenType;
    private Integer availableSeats;
    private Long ticketPrice;
    private LocalTime showTime;
}
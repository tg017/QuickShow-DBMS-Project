package quickshow.dbms.project.repository.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class BookingDetailsData {

    private Integer bookingId;

    private Long totalAmount;
    private Integer totalSeatCount;
    private String bookingStatus;

    private Integer showId;
    private LocalDate showDate;
    private LocalTime showTime;

    private Integer movieId;
    private String movieTitle;

    private Integer theatreId;
    private String theatreName;
    private String city;

    private Integer screenId;
    private String screenName;
    private String screenType;
}
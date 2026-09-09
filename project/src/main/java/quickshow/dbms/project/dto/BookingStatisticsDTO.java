package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatisticsDTO {

    private Long totalBookings;

    private Long confirmedBookings;

    private Long cancelledBookings;

    private Long pendingBookings;

    private Long ticketsSold;

    private Long cancelledTickets;

    private Double averageSeatsPerBooking;
}
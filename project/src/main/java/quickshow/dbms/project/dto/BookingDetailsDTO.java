package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BookingDetailsDTO {

    private Integer bookingId;

    private ShowSummaryDTO show;

    private MovieBookingSummaryDTO movie;

    private TheatreSummaryDTO theatre;

    private ScreenSummaryDTO screen;

    private List<SelectedSeatDTO> seats;

    private Integer totalSeatCount;

    private Long totalAmount;

    private String bookingStatus;

    private PaymentSummaryDTO payment;
}
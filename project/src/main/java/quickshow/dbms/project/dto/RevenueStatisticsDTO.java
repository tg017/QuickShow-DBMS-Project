package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RevenueStatisticsDTO {

    private BigDecimal totalRevenue;

    private BigDecimal averageBookingValue;

    private BigDecimal averageTicketPrice;

    private Long successfulPayments;

    private Long failedPayments;

    private Long cancelledBookings;
}
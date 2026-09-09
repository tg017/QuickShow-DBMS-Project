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
public class AdminStatisticsSummaryDTO {

    private BigDecimal totalRevenue;

    private Long totalBookings;

    private Long ticketsSold;

    private Long activeShows;

    private Long totalMovies;

    private Long totalTheatres;

    private Long totalCustomers;
}
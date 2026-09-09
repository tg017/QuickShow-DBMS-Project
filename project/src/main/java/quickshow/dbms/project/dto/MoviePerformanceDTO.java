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
public class MoviePerformanceDTO {

    private Integer movieId;

    private String title;

    private Long ticketsSold;

    private Long bookings;

    private BigDecimal revenue;
}
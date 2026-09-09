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
public class TheatrePerformanceDTO {

    private Integer theatreId;

    private String theatreName;

    private Long totalShows;

    private Long ticketsSold;

    private BigDecimal revenue;
}
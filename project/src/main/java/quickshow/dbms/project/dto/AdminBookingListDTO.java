package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminBookingListDTO {

    private Integer bookingId;

    private String movieName;

    private String theatreName;

    private String bookingStatus;

    private Long totalAmount;
}
package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingCheckoutRequestDTO {

    private Integer customerId;

    private Integer showId;

    private List<Integer> seatIds;

    private String paymentMethod;
}
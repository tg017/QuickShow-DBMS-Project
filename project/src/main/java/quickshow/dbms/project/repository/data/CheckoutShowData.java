package quickshow.dbms.project.repository.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CheckoutShowData {

    private Integer showId;
    private Integer screenId;
    private Integer movieId;
    private Long ticketPrice;
    private Integer availableSeats;
    private String showStatus;
}
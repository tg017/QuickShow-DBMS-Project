
package quickshow.dbms.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BookedSeatsId implements Serializable {

    @Column(name = "BookingID")
    private Integer bookingId;

    @Column(name = "ShowID")
    private Integer showId;

    @Column(name = "ScreenID")
    private Integer screenId;

    @Column(name = "SeatID")
    private Integer seatId;
}

package model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BookedSeats")
public class BookedSeats {

    @EmbeddedId
    private BookedSeatsId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bookingId")
    @JoinColumn(name = "BookingID", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(
                    name = "ShowID",
                    referencedColumnName = "ShowID",
                    insertable = false,
                    updatable = false
            ),
            @JoinColumn(
                    name = "ScreenID",
                    referencedColumnName = "ScreenID",
                    insertable = false,
                    updatable = false
            ),
            @JoinColumn(
                    name = "SeatID",
                    referencedColumnName = "SeatID",
                    insertable = false,
                    updatable = false
            )
    })
    private ShowSeatAllocation seatAllocation;
}

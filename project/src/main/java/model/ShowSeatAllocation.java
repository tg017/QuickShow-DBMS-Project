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
@Table(name = "ShowSeatAllocates")
public class ShowSeatAllocation {

    @EmbeddedId
    private ShowSeatAllocationId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("showId")
    @JoinColumn(name = "ShowID", nullable = false)
    private Show show;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
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
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private SeatAllocationStatus status = SeatAllocationStatus.AVAILABLE;
}
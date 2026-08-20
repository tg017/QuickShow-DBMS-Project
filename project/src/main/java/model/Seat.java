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
@Table(name = "Seat")
public class Seat {

    @EmbeddedId
    private SeatId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("screenId")
    @JoinColumn(name = "ScreenID", nullable = false)
    private Screen screen;

    @Column(name = "RowNo", nullable = false)
    private Integer rowNo;

    @Column(name = "SeatNo", nullable = false)
    private Integer seatNo;
}
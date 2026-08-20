package model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`Show`")
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ShowID")
    private Integer showId;

    @Column(name = "ShowDate", nullable = false)
    private LocalDate showDate;

    @Column(name = "ShowTime", nullable = false)
    private LocalTime showTime;

    @Column(name = "TicketPrice", nullable = false)
    private Long ticketPrice;

    @Column(name = "AvailableSeats", nullable = false)
    private Integer availableSeats;

    @Enumerated(EnumType.STRING)
    @Column(name = "ShowStatus", nullable = false)
    private ShowStatus showStatus = ShowStatus.SCHEDULED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MovieID", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ScreenID", nullable = false)
    private Screen screen;

    @OneToMany(mappedBy = "show")
    private List<ShowSeatAllocation> seatAllocations = new ArrayList<>();
}
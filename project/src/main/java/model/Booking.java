package model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BookingID")
    private Integer bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false)
    private Customer customer;

    @Column(name = "BookingDateTime")
    private LocalDateTime bookingDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "BookingStatus", nullable = false)
    private BookingStatus bookingStatus;

    @Column(name = "TotalAmount", nullable = false)
    private Long totalAmount;

    @Column(name = "TotalSeatsCount", nullable = false)
    private Integer totalSeatsCount;

    @OneToMany(mappedBy = "booking")
    private List<BookedSeats> bookedSeats = new ArrayList<>();

    @OneToMany(mappedBy = "booking")
    private List<Payment> payments = new ArrayList<>();
}
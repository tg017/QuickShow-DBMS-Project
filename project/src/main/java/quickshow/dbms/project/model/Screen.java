package quickshow.dbms.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Screen")
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ScreenID")
    private Integer screenId;

    @Column(name = "ScreenName", nullable = false, length = 50)
    private String name;

    @Convert(converter = ScreenTypeConverter.class)
    @Column(name = "ScreenType", nullable = false)
    private ScreenType screenType;

    @Column(name = "SeatingCapacity", nullable = false)
    private Integer capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TheatreID", nullable = false)
    private Theatre theatre;

    @OneToMany(mappedBy = "screen")
    private List<Seat> seats = new ArrayList<>();

    @OneToMany(mappedBy = "screen")
    private List<Show> shows = new ArrayList<>();
}
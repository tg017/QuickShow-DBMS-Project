package model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Movie")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MovieID")
    private Integer movieId;

    @Column(name = "Title", nullable = false, length = 50)
    private String title;

    @Column(name = "Poster", length = 255)
    private String poster;

    @Column(name = "Language", nullable = false, length = 20)
    private String language;

    @Column(name = "Genre", length = 50)
    private String genre;

    @Column(name = "Duration", nullable = false)
    private Integer duration;

    @Column(name = "ReleaseDate", nullable = false)
    private LocalDate releaseDate;

    @Column(name = "IMDbRating", precision = 3, scale = 1)
    private BigDecimal imdbRating;

    @Enumerated(EnumType.STRING)
    @Column(name = "Certificate")
    private Certificate certificate;

    @Column(name = "Director", length = 50)
    private String director;

    @Lob
    @Column(name = "Description")
    private String description;

    @OneToMany(mappedBy = "movie")
    private List<MovieCast> cast = new ArrayList<>();
}

/*
    MovieCast is essentially dependent on Movie, but later we're going to have
    several relationships involving Movie, and we should decide cascading behavior based on
    the actual business operations.
*/
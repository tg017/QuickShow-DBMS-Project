package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class MovieDetailsDTO {

    private Integer movieId;
    private String title;
    private String poster;
    private String language;
    private String genre;
    private Integer duration;
    private LocalDate releaseDate;
    private BigDecimal imdbRating;
    private String certificate;
    private String director;
    private String description;
    private List<String> cast;
}
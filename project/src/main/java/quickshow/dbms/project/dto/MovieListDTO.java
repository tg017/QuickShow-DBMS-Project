package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class MovieListDTO {

    private Integer movieId;
    private String title;
    private String poster;
    private String genre;
    private BigDecimal imdbRating;
}
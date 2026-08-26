package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MovieSummaryDTO {

    private Integer movieId;
    private String title;
    private String poster;
}
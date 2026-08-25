package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TheatreShowsDTO {

    private Integer theatreId;
    private String theatreName;
    private List<MovieShowDTO> shows;
}
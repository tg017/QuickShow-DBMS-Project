package quickshow.dbms.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quickshow.dbms.project.dto.SeatLayoutDTO;
import quickshow.dbms.project.dto.TheatreShowsDTO;
import quickshow.dbms.project.service.ShowService;
import quickshow.dbms.project.service.TheatreService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/shows")
public class ShowController {
    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    @GetMapping("/movie/{movieId}/search")
    public ResponseEntity<List<TheatreShowsDTO>> searchShowsByMovie(
            @PathVariable Integer movieId,
            @RequestParam(required = false) LocalDate date
    ) {

        return ResponseEntity.ok(
                showService.getShowsByMovieAndDate(
                        movieId,
                        date
                )
        );
    }

    @GetMapping("/{showId}/seats")
    public ResponseEntity<SeatLayoutDTO> getSeatLayout(
            @PathVariable Integer showId
    ) {

        return ResponseEntity.ok(
                showService.getSeatLayout(showId)
        );
    }
}

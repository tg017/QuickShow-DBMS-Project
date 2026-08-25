package quickshow.dbms.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quickshow.dbms.project.dto.TheatreListDTO;
import quickshow.dbms.project.dto.TheatreShowDTO;
import quickshow.dbms.project.service.TheatreService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/theatres")
public class TheatreController {

    private final TheatreService theatreService;

    public TheatreController(TheatreService theatreService) {
        this.theatreService = theatreService;
    }

    @GetMapping
    public ResponseEntity<List<TheatreListDTO>> getTheatres() {

        return ResponseEntity.ok(
                theatreService.getTheatresWithScheduledShows()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<TheatreListDTO>> searchTheatres(
            @RequestParam String city
    ) {

        return ResponseEntity.ok(
                theatreService.getTheatresByCity(city)
        );
    }

    @GetMapping("/{theatreId}/shows/search")
    public ResponseEntity<List<TheatreShowDTO>> searchShows(
            @PathVariable Integer theatreId,
            @RequestParam(required = false) LocalDate date
    ) {

        return ResponseEntity.ok(
                theatreService.getShowsByTheatreAndDate(
                        theatreId,
                        date
                )
        );
    }
}
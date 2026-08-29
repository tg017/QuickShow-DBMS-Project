package quickshow.dbms.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import quickshow.dbms.project.dto.MovieDetailsDTO;
import quickshow.dbms.project.dto.MovieListDTO;
import quickshow.dbms.project.model.Movie;
import quickshow.dbms.project.service.AdminMovieService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/movies")
public class AdminMovieController {

    private final AdminMovieService adminMovieService;

    public AdminMovieController(
            AdminMovieService adminMovieService
    ) {
        this.adminMovieService = adminMovieService;
    }

    @GetMapping
    public ResponseEntity<List<MovieListDTO>> getAllMovies() {

        return ResponseEntity.ok(
                adminMovieService.getAllMovies()
        );
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDetailsDTO> getMovieById(
            @PathVariable Integer movieId) {

        MovieDetailsDTO movie =
                adminMovieService.getMovieById(movieId);

        if (movie == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(movie);
    }

    @PostMapping
    public ResponseEntity<Movie> createMovie(
            @RequestBody Movie movie
    ) {

        Movie createdMovie =
                adminMovieService.createMovie(movie);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdMovie);
    }

    @PutMapping("/{movieId}")
    public ResponseEntity<Movie> updateMovie(
            @PathVariable Integer movieId,
            @RequestBody Movie movie
    ) {

        Movie updatedMovie =
                adminMovieService.updateMovie(
                        movieId,
                        movie
                );

        if (updatedMovie == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedMovie);
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<String> deleteMovie(
            @PathVariable Integer movieId
    ) {

        String result =
                adminMovieService.deleteMovie(movieId);

        if ("NOT_FOUND".equals(result)) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        if ("HAS_SHOWS".equals(result)) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            "Movie cannot be deleted because it has associated shows."
                    );
        }

        return ResponseEntity.ok(
                "Movie deleted successfully."
        );
    }
}
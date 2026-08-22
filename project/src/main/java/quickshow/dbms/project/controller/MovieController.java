package quickshow.dbms.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quickshow.dbms.project.model.Movie;
import quickshow.dbms.project.service.MovieService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

        private final MovieService movieService;

        public MovieController(MovieService movieService) {
                this.movieService = movieService;
        }

        // =========================================================
        // GET ALL MOVIES
        // GET /movies
        // =========================================================

        @GetMapping
        public ResponseEntity<List<Movie>> getAllMovies() {

                return ResponseEntity.ok(
                                movieService.getAllMovies());
        }

        // =========================================================
        // GET MOVIE BY ID
        // GET /movies/{id}
        // =========================================================

        @GetMapping("/{id}")
        public ResponseEntity<Movie> getMovieById(
                        @PathVariable Integer id) {

                return ResponseEntity.ok(
                                movieService.getMovieById(id));
        }

        // =========================================================
        // COMBINED MOVIE SEARCH
        //
        // GET /api/movies/search
        //
        // Examples:
        // /api/movies/search?title=dark
        // /api/movies/search?language=English
        // /api/movies/search?genre=Sci-Fi&minRating=8.0
        // /api/movies/search?title=dark&language=English&certificate=UA_13_PLUS
        // =========================================================

        @GetMapping("/search")
        public ResponseEntity<List<Movie>> searchMovies(

                        @RequestParam(required = false) String title,

                        @RequestParam(required = false) String language,

                        @RequestParam(required = false) String genre,

                        @RequestParam(required = false) String certificate,

                        @RequestParam(required = false) BigDecimal minRating) {

                return ResponseEntity.ok(
                                movieService.searchMovies(
                                                title,
                                                language,
                                                genre,
                                                certificate,
                                                minRating));
        }

        // =========================================================
        // CREATE
        // POST /movies
        // =========================================================

        @PostMapping
        public ResponseEntity<Movie> createMovie(
                        @RequestBody Movie movie) {

                Movie createdMovie = movieService.createMovie(movie);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(createdMovie);
        }

        // =========================================================
        // UPDATE
        // PUT /movies/{id}
        // =========================================================

        @PutMapping("/{id}")
        public ResponseEntity<Movie> updateMovie(
                        @PathVariable Integer id,
                        @RequestBody Movie movie) {

                Movie updatedMovie = movieService.updateMovie(
                                id,
                                movie);

                return ResponseEntity.ok(
                                updatedMovie);
        }

        // =========================================================
        // DELETE
        // DELETE /movies/{id}
        // =========================================================

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteMovie(
                        @PathVariable Integer id) {

                movieService.deleteMovie(id);

                return ResponseEntity.noContent()
                                .build();
        }
}
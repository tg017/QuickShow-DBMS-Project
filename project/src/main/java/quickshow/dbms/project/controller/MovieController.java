package quickshow.dbms.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quickshow.dbms.project.dto.MovieDetailsDTO;
import quickshow.dbms.project.dto.MovieListDTO;
import quickshow.dbms.project.model.Certificate;
import quickshow.dbms.project.service.MovieService;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

        private final MovieService movieService;

        public MovieController(MovieService movieService) {
                this.movieService = movieService;
        }

        @GetMapping
        public ResponseEntity<List<MovieListDTO>> getMovies() {

                return ResponseEntity.ok(
                        movieService.getMoviesWithinThirtyDays()
                );
        }

        @GetMapping("/search")
        public ResponseEntity<List<MovieListDTO>> searchMovies(

                @RequestParam(required = false)
                String title,

                @RequestParam(required = false)
                String language,

                @RequestParam(required = false)
                Certificate certificate,

                @RequestParam(required = false)
                String genre
        ) {

                return ResponseEntity.ok(
                        movieService.searchMovies(
                                title,
                                language,
                                certificate,
                                genre
                        )
                );
        }

        @GetMapping("/{movieId}")
        public ResponseEntity<MovieDetailsDTO> getMovieById(
                @PathVariable Integer movieId
        ) {

                return ResponseEntity.ok(
                        movieService.getMovieById(movieId)
                );
        }
}
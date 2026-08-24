package quickshow.dbms.project.service;

import org.springframework.stereotype.Service;
import quickshow.dbms.project.dto.MovieDetailsDTO;
import quickshow.dbms.project.dto.MovieListDTO;
import quickshow.dbms.project.exception.ResourceNotFoundException;
import quickshow.dbms.project.model.Certificate;
import quickshow.dbms.project.repository.MovieRepository;

import java.util.List;

@Service
public class MovieService {

        private final MovieRepository movieRepository;

        public MovieService(MovieRepository movieRepository) {
                this.movieRepository = movieRepository;
        }

        public List<MovieListDTO> getMoviesWithinThirtyDays() {

                return movieRepository.findMoviesWithinThirtyDays();
        }

        public List<MovieListDTO> searchMovies(
                String title,
                String language,
                Certificate certificate,
                String genre
        ) {

                return movieRepository.searchMovies(
                        title,
                        language,
                        certificate,
                        genre
                );
        }

        public MovieDetailsDTO getMovieById(
                Integer movieId
        ) {

                if (movieId == null) {
                        throw new IllegalArgumentException(
                                "Movie ID cannot be null"
                        );
                }

                MovieDetailsDTO movie = movieRepository.findMovieDetailsById(
                        movieId
                );

                if (movie == null) {
                        throw new ResourceNotFoundException(
                                "Movie not found"
                        );
                }

                return movie;
        }
}
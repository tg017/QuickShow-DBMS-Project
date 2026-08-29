package quickshow.dbms.project.service;

import org.springframework.stereotype.Service;

import quickshow.dbms.project.model.MovieCast;
import quickshow.dbms.project.model.MovieCastId;
import quickshow.dbms.project.repository.MovieCastRepository;

import java.util.List;

@Service
public class AdminMovieCastService {

    private final MovieCastRepository movieCastRepository;

    public AdminMovieCastService(
            MovieCastRepository movieCastRepository
    ) {
        this.movieCastRepository = movieCastRepository;
    }


    // =========================================================
    // GET CAST
    // =========================================================

    public List<MovieCast> getMovieCast(
            Integer movieId
    ) {

        return movieCastRepository.findByMovie(movieId);
    }


    // =========================================================
    // ADD ACTOR
    // =========================================================

    public String addActor(
            Integer movieId,
            String actor
    ) {

        // Movie must exist
        if (!movieCastRepository.movieExists(movieId)) {
            return "MOVIE_NOT_FOUND";
        }

        // Actor name must not be empty
        if (actor == null || actor.trim().isEmpty()) {
            return "INVALID_ACTOR";
        }

        actor = actor.trim();

        MovieCastId id = new MovieCastId();

        id.setMovieId(movieId);
        id.setActor(actor);

        // Same actor cannot be added twice
        if (movieCastRepository.existsById(id)) {
            return "ACTOR_EXISTS";
        }

        MovieCast movieCast = new MovieCast();

        movieCast.setId(id);

        movieCastRepository.create(movieCast);

        return "CREATED";
    }


    // =========================================================
    // DELETE ACTOR
    // =========================================================

    public boolean deleteActor(
            Integer movieId,
            String actor
    ) {

        MovieCastId id = new MovieCastId();

        id.setMovieId(movieId);
        id.setActor(actor);

        if (!movieCastRepository.existsById(id)) {
            return false;
        }

        int deleted =
                movieCastRepository.deleteById(id);

        return deleted > 0;
    }
}
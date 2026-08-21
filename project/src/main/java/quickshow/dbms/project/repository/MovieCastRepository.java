package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import quickshow.dbms.project.model.MovieCast;
import quickshow.dbms.project.model.MovieCastId;

import java.util.List;

@Repository
public class MovieCastRepository {

    private final JdbcTemplate jdbcTemplate;

    public MovieCastRepository(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // =========================================================
    // CREATE
    // =========================================================

    public MovieCast create(
            MovieCast movieCast
    ) {

        String sql = """
                INSERT INTO MovieCast
                    (
                        MovieID,
                        Actor
                    )
                VALUES
                    (?, ?)
                """;

        MovieCastId id =
                movieCast.getId();

        jdbcTemplate.update(
                sql,
                id.getMovieId(),
                id.getActor()
        );

        return movieCast;
    }


    // =========================================================
    // READ BY COMPOSITE ID
    // =========================================================

    public MovieCast findById(
            MovieCastId id
    ) {

        String sql = """
                SELECT
                    MovieID,
                    Actor
                FROM MovieCast
                WHERE MovieID = ?
                  AND Actor = ?
                """;

        List<MovieCast> movieCasts =
                jdbcTemplate.query(
                        sql,
                        new MovieCastRowMapper(),
                        id.getMovieId(),
                        id.getActor()
                );

        if (movieCasts.isEmpty()) {
            return null;
        }

        return movieCasts.get(0);
    }


    // =========================================================
    // READ ALL
    // =========================================================

    public List<MovieCast> findAll() {

        String sql = """
                SELECT
                    MovieID,
                    Actor
                FROM MovieCast
                ORDER BY MovieID, Actor
                """;

        return jdbcTemplate.query(
                sql,
                new MovieCastRowMapper()
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    /*
     * The primary key consists of MovieID + Actor.
     *
     * Therefore there is no ordinary non-key field to update.
     *
     * We don't need an update() method here.
     */


    // =========================================================
    // DELETE
    // =========================================================

    public int deleteById(
            MovieCastId id
    ) {

        String sql = """
                DELETE FROM MovieCast
                WHERE MovieID = ?
                  AND Actor = ?
                """;

        return jdbcTemplate.update(
                sql,
                id.getMovieId(),
                id.getActor()
        );
    }


    // =========================================================
    // EXISTS
    // =========================================================

    public boolean existsById(
            MovieCastId id
    ) {

        String sql = """
                SELECT COUNT(*)
                FROM MovieCast
                WHERE MovieID = ?
                  AND Actor = ?
                """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        id.getMovieId(),
                        id.getActor()
                );

        return count != null && count > 0;
    }


    // =========================================================
    // FIND ALL ACTORS FOR A MOVIE
    // =========================================================

    public List<MovieCast> findByMovie(
            Integer movieId
    ) {

        String sql = """
                SELECT
                    MovieID,
                    Actor
                FROM MovieCast
                WHERE MovieID = ?
                ORDER BY Actor
                """;

        return jdbcTemplate.query(
                sql,
                new MovieCastRowMapper(),
                movieId
        );
    }


    // =========================================================
    // FIND MOVIES FOR AN ACTOR
    // =========================================================

    public List<MovieCast> findByActor(
            String actor
    ) {

        String sql = """
                SELECT
                    MovieID,
                    Actor
                FROM MovieCast
                WHERE Actor = ?
                ORDER BY MovieID
                """;

        return jdbcTemplate.query(
                sql,
                new MovieCastRowMapper(),
                actor
        );
    }
}
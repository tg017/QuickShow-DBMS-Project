package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import quickshow.dbms.project.model.MovieCast;
import quickshow.dbms.project.model.MovieCastId;

import java.util.List;

@Repository
public class MovieCastRepository {

    private final JdbcTemplate jdbcTemplate;

    public MovieCastRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // =========================================================
    // GET CAST OF MOVIE
    // =========================================================

    public List<MovieCast> findByMovie(Integer movieId) {

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
                (rs, rowNum) -> {

                    MovieCastId id = new MovieCastId();

                    id.setMovieId(
                            rs.getInt("MovieID")
                    );

                    id.setActor(
                            rs.getString("Actor")
                    );

                    MovieCast movieCast = new MovieCast();

                    movieCast.setId(id);

                    return movieCast;
                },
                movieId
        );
    }


    // =========================================================
    // CHECK WHETHER MOVIE EXISTS
    // =========================================================

    public boolean movieExists(Integer movieId) {

        String sql = """
                SELECT COUNT(*)
                FROM Movie
                WHERE MovieID = ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                movieId
        );

        return count != null && count > 0;
    }


    // =========================================================
    // CHECK WHETHER ACTOR ALREADY EXISTS
    // =========================================================

    public boolean existsById(MovieCastId id) {

        String sql = """
                SELECT COUNT(*)
                FROM MovieCast
                WHERE MovieID = ?
                  AND Actor = ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                id.getMovieId(),
                id.getActor()
        );

        return count != null && count > 0;
    }


    // =========================================================
    // ADD ACTOR
    // =========================================================

    public void create(MovieCast movieCast) {

        String sql = """
                INSERT INTO MovieCast
                (
                    MovieID,
                    Actor
                )
                VALUES (?, ?)
                """;

        jdbcTemplate.update(
                sql,
                movieCast.getId().getMovieId(),
                movieCast.getId().getActor()
        );
    }


    // =========================================================
    // DELETE ACTOR
    // =========================================================

    public int deleteById(MovieCastId id) {

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
}
package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import quickshow.dbms.project.model.Movie;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class MovieRepository {

    private final JdbcTemplate jdbcTemplate;

    public MovieRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // =========================================================
    // CREATE
    // =========================================================

    public Movie create(Movie movie) {

        String sql = """
                INSERT INTO Movie
                    (
                        Title,
                        Poster,
                        Language,
                        Genre,
                        Duration,
                        ReleaseDate,
                        IMDbRating,
                        Certificate,
                        Director,
                        Description
                    )
                VALUES
                    (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder =
                new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement statement =
                    connection.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    );

            statement.setString(1, movie.getTitle());
            statement.setString(2, movie.getPoster());
            statement.setString(3, movie.getLanguage());
            statement.setString(4, movie.getGenre());
            statement.setObject(5, movie.getDuration());
            statement.setObject(6, movie.getReleaseDate());
            statement.setObject(7, movie.getImdbRating());

            if (movie.getCertificate() != null) {
                statement.setString(
                        8,
                        movie.getCertificate().name()
                );
            } else {
                statement.setNull(
                        8,
                        java.sql.Types.VARCHAR
                );
            }

            statement.setString(9, movie.getDirector());
            statement.setString(10, movie.getDescription());

            return statement;

        }, keyHolder);

        Number generatedId =
                keyHolder.getKey();

        if (generatedId == null) {
            throw new IllegalStateException(
                    "MovieID was not generated"
            );
        }

        movie.setMovieId(
                generatedId.intValue()
        );

        return movie;
    }


    // =========================================================
    // READ BY ID
    // =========================================================

    public Movie findById(Integer movieId) {

        String sql = """
                SELECT
                    MovieID,
                    Title,
                    Poster,
                    Language,
                    Genre,
                    Duration,
                    ReleaseDate,
                    IMDbRating,
                    Certificate,
                    Director,
                    Description
                FROM Movie
                WHERE MovieID = ?
                """;

        List<Movie> movies =
                jdbcTemplate.query(
                        sql,
                        new MovieRowMapper(),
                        movieId
                );

        if (movies.isEmpty()) {
            return null;
        }

        return movies.get(0);
    }


    // =========================================================
    // READ ALL
    // =========================================================

    public List<Movie> findAll() {

        String sql = """
                SELECT
                    MovieID,
                    Title,
                    Poster,
                    Language,
                    Genre,
                    Duration,
                    ReleaseDate,
                    IMDbRating,
                    Certificate,
                    Director,
                    Description
                FROM Movie
                ORDER BY MovieID
                """;

        return jdbcTemplate.query(
                sql,
                new MovieRowMapper()
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    public int update(Movie movie) {

        String sql = """
                UPDATE Movie
                SET
                    Title = ?,
                    Poster = ?,
                    Language = ?,
                    Genre = ?,
                    Duration = ?,
                    ReleaseDate = ?,
                    IMDbRating = ?,
                    Certificate = ?,
                    Director = ?,
                    Description = ?
                WHERE MovieID = ?
                """;

        String certificate = null;

        if (movie.getCertificate() != null) {
            certificate =
                    movie.getCertificate().name();
        }

        return jdbcTemplate.update(
                sql,
                movie.getTitle(),
                movie.getPoster(),
                movie.getLanguage(),
                movie.getGenre(),
                movie.getDuration(),
                movie.getReleaseDate(),
                movie.getImdbRating(),
                certificate,
                movie.getDirector(),
                movie.getDescription(),
                movie.getMovieId()
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    public int deleteById(Integer movieId) {

        String sql = """
                DELETE FROM Movie
                WHERE MovieID = ?
                """;

        return jdbcTemplate.update(
                sql,
                movieId
        );
    }


    // =========================================================
    // EXISTS
    // =========================================================

    public boolean existsById(Integer movieId) {

        String sql = """
                SELECT COUNT(*)
                FROM Movie
                WHERE MovieID = ?
                """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        movieId
                );

        return count != null && count > 0;
    }


    // =========================================================
    // FIND BY LANGUAGE
    // =========================================================

    public List<Movie> findByLanguage(
            String language
    ) {

        String sql = """
                SELECT
                    MovieID,
                    Title,
                    Poster,
                    Language,
                    Genre,
                    Duration,
                    ReleaseDate,
                    IMDbRating,
                    Certificate,
                    Director,
                    Description
                FROM Movie
                WHERE Language = ?
                ORDER BY Title
                """;

        return jdbcTemplate.query(
                sql,
                new MovieRowMapper(),
                language
        );
    }


    // =========================================================
    // FIND BY GENRE AND MINIMUM RATING
    // =========================================================

    public List<Movie> findByGenreAndMinimumRating(
            String genre,
            java.math.BigDecimal minimumRating
    ) {

        String sql = """
                SELECT
                    MovieID,
                    Title,
                    Poster,
                    Language,
                    Genre,
                    Duration,
                    ReleaseDate,
                    IMDbRating,
                    Certificate,
                    Director,
                    Description
                FROM Movie
                WHERE Genre = ?
                  AND IMDbRating >= ?
                ORDER BY IMDbRating DESC
                """;

        return jdbcTemplate.query(
                sql,
                new MovieRowMapper(),
                genre,
                minimumRating
        );
    }
}
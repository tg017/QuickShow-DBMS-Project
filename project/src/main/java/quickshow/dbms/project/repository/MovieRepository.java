package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import quickshow.dbms.project.model.Movie;
import quickshow.dbms.project.model.MovieCast;
import quickshow.dbms.project.model.MovieCastId;
import quickshow.dbms.project.model.Show;

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

                KeyHolder keyHolder = new GeneratedKeyHolder();

                jdbcTemplate.update(connection -> {

                        PreparedStatement statement = connection.prepareStatement(
                                        sql,
                                        Statement.RETURN_GENERATED_KEYS);

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
                                                movie.getCertificate().name());
                        } else {
                                statement.setNull(
                                                8,
                                                java.sql.Types.VARCHAR);
                        }

                        statement.setString(9, movie.getDirector());
                        statement.setString(10, movie.getDescription());

                        return statement;

                }, keyHolder);

                Number generatedId = keyHolder.getKey();

                if (generatedId == null) {
                        throw new IllegalStateException(
                                        "MovieID was not generated");
                }

                movie.setMovieId(
                                generatedId.intValue());

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

                Movie movie = movies.get(0);


                // =========================================================
                // LOAD CAST
                // =========================================================

                String castSql = """
        SELECT
            MovieID,
            Actor
        FROM MovieCast
        WHERE MovieID = ?
        ORDER BY Actor
        """;

                List<MovieCast> cast =
                        jdbcTemplate.query(
                                castSql,
                                (rs, rowNum) -> {

                                        MovieCast movieCast =
                                                new MovieCast();

                                        MovieCastId id =
                                                new MovieCastId();

                                        id.setMovieId(
                                                rs.getInt("MovieID")
                                        );

                                        id.setActor(
                                                rs.getString("Actor")
                                        );

                                        movieCast.setId(id);

                                        return movieCast;
                                },
                                movieId
                        );

                movie.setCast(cast);


                // =========================================================
                // LOAD SHOWS
                // =========================================================

                String showSql = """
            SELECT
                ShowID,
                ShowDate,
                ShowTime,
                TicketPrice,
                AvailableSeats,
                ShowStatus,
                MovieID,
                ScreenID
            FROM `Show`
            WHERE MovieID = ?
            ORDER BY ShowDate, ShowTime
            """;

                List<Show> shows =
                        jdbcTemplate.query(
                                showSql,
                                new ShowRowMapper(),
                                movieId
                        );

                movie.setShows(shows);

                return movie;
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
                                new MovieRowMapper());
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
                        certificate = movie.getCertificate().name();
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
                                movie.getMovieId());
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
                                movieId);
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

                Integer count = jdbcTemplate.queryForObject(
                                sql,
                                Integer.class,
                                movieId);

                return count != null && count > 0;
        }

        // =========================================================
        // COMBINED SEARCH
        // GET /api/movies/search
        // =========================================================

        public List<Movie> searchMovies(
                        String title,
                        String language,
                        String genre,
                        String certificate,
                        java.math.BigDecimal minRating) {

                StringBuilder sql = new StringBuilder("""
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
                                WHERE 1 = 1
                                """);

                List<Object> params = new java.util.ArrayList<>();

                // Title search
                if (title != null && !title.isBlank()) {

                        sql.append("""
                                        AND Title LIKE ?
                                        """);

                        params.add("%" + title.trim() + "%");
                }

                // Language filter
                if (language != null && !language.isBlank()) {

                        sql.append("""
                                        AND Language = ?
                                        """);

                        params.add(language.trim());
                }

                // Genre filter
                if (genre != null && !genre.isBlank()) {

                        sql.append("""
                                        AND Genre = ?
                                        """);

                        params.add(genre.trim());
                }

                // Certificate filter
                if (certificate != null && !certificate.isBlank()) {

                        sql.append("""
                                        AND Certificate = ?
                                        """);

                        params.add(certificate.trim());
                }

                // Minimum IMDb rating
                if (minRating != null) {

                        sql.append("""
                                        AND IMDbRating >= ?
                                        """);

                        params.add(minRating);
                }

                sql.append("""
                                ORDER BY Title
                                """);

                return jdbcTemplate.query(
                                sql.toString(),
                                new MovieRowMapper(),
                                params.toArray());
        }
}
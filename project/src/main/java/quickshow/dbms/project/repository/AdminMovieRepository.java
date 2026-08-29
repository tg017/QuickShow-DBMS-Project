package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import quickshow.dbms.project.dto.MovieDetailsDTO;
import quickshow.dbms.project.dto.MovieListDTO;
import quickshow.dbms.project.model.Certificate;
import quickshow.dbms.project.model.Movie;

import java.util.List;

@Repository
public class AdminMovieRepository {

    private final JdbcTemplate jdbcTemplate;

    public AdminMovieRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<MovieListDTO> findAllMovies() {

        String sql = """
                SELECT
                    MovieID,
                    Title,
                    Poster,
                    Genre,
                    ImdbRating
                FROM Movie
                ORDER BY MovieID
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new MovieListDTO(
                        rs.getInt("MovieID"),
                        rs.getString("Title"),
                        rs.getString("Poster"),
                        rs.getString("Genre"),
                        rs.getBigDecimal("ImdbRating")
                )
        );
    }

    public MovieDetailsDTO findMovieDetailsById(Integer movieId) {

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

        List<MovieDetailsDTO> movies = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    MovieDetailsDTO movie = new MovieDetailsDTO();

                    movie.setMovieId(
                            rs.getInt("MovieID")
                    );

                    movie.setTitle(
                            rs.getString("Title")
                    );

                    movie.setPoster(
                            rs.getString("Poster")
                    );

                    movie.setLanguage(
                            rs.getString("Language")
                    );

                    movie.setGenre(
                            rs.getString("Genre")
                    );

                    movie.setDuration(
                            rs.getInt("Duration")
                    );

                    if (rs.getDate("ReleaseDate") != null) {
                        movie.setReleaseDate(
                                rs.getDate("ReleaseDate").toLocalDate()
                        );
                    }

                    movie.setImdbRating(
                            rs.getBigDecimal("IMDbRating")
                    );

                    String certificate =
                            rs.getString("Certificate");

                    if (certificate != null) {
                        movie.setCertificate(
                                String.valueOf(Certificate.valueOf(certificate))
                        );
                    }

                    movie.setDirector(
                            rs.getString("Director")
                    );

                    movie.setDescription(
                            rs.getString("Description")
                    );

                    return movie;
                },
                movieId
        );

        if (movies.isEmpty()) {
            return null;
        }

        MovieDetailsDTO movie = movies.get(0);

        String castSql = """
            SELECT Actor
            FROM MovieCast
            WHERE MovieID = ?
            ORDER BY Actor
            """;

        List<String> cast = jdbcTemplate.query(
                castSql,
                (rs, rowNum) ->
                        rs.getString("Actor"),
                movieId
        );

        movie.setCast(cast);

        return movie;
    }

    public Integer createMovie(Movie movie) {

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
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        jdbcTemplate.update(
                sql,
                movie.getTitle(),
                movie.getPoster(),
                movie.getLanguage(),
                movie.getGenre(),
                movie.getDuration(),
                movie.getReleaseDate(),
                movie.getImdbRating(),
                movie.getCertificate() != null
                        ? movie.getCertificate().name()
                        : null,
                movie.getDirector(),
                movie.getDescription()
        );

        return jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Integer.class
        );
    }

    public boolean existsById(Integer movieId) {

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

    public int updateMovie(Movie movie) {

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

        return jdbcTemplate.update(
                sql,
                movie.getTitle(),
                movie.getPoster(),
                movie.getLanguage(),
                movie.getGenre(),
                movie.getDuration(),
                movie.getReleaseDate(),
                movie.getImdbRating(),
                movie.getCertificate() != null
                        ? movie.getCertificate().name()
                        : null,
                movie.getDirector(),
                movie.getDescription(),
                movie.getMovieId()
        );
    }

    public boolean hasShows(Integer movieId) {

        String sql = """
            SELECT COUNT(*)
            FROM `Show`
            WHERE MovieID = ?
            """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                movieId
        );

        return count != null && count > 0;
    }

    public boolean hasCast(Integer movieId) {

        String sql = """
            SELECT COUNT(*)
            FROM MovieCast
            WHERE MovieID = ?
            """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                movieId
        );

        return count != null && count > 0;
    }

    public void deleteMovieCast(Integer movieId) {

        String sql = """
            DELETE FROM MovieCast
            WHERE MovieID = ?
            """;

        jdbcTemplate.update(sql, movieId);
    }

    public int deleteMovie(Integer movieId) {

        String sql = """
            DELETE FROM Movie
            WHERE MovieID = ?
            """;

        return jdbcTemplate.update(
                sql,
                movieId
        );
    }

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
}
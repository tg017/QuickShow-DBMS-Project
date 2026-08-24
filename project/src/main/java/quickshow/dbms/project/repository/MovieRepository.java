package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import quickshow.dbms.project.dto.MovieDetailsDTO;
import quickshow.dbms.project.dto.MovieListDTO;
import quickshow.dbms.project.model.Certificate;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MovieRepository {

    private final JdbcTemplate jdbcTemplate;

    public MovieRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<MovieListDTO> findMoviesWithinThirtyDays() {

        String sql = """
                SELECT
                    MovieID,
                    Title,
                    Poster,
                    Genre,
                    IMDbRating
                FROM Movie
                WHERE ReleaseDate BETWEEN
                      DATE_SUB(CURDATE(), INTERVAL 30 DAY)
                      AND
                      DATE_ADD(CURDATE(), INTERVAL 30 DAY)
                ORDER BY ReleaseDate
                """;

        return jdbcTemplate.query(
                sql,
                new MovieListRowMapper()
        );
    }

    public List<MovieListDTO> searchMovies(
            String title,
            String language,
            Certificate certificate,
            String genre
    ) {

        StringBuilder sql = new StringBuilder("""
            SELECT
                MovieID,
                Title,
                Poster,
                Genre,
                IMDbRating
            FROM Movie
            WHERE ReleaseDate BETWEEN
                  DATE_SUB(CURDATE(), INTERVAL 30 DAY)
                  AND
                  DATE_ADD(CURDATE(), INTERVAL 30 DAY)
            """);

        List<Object> parameters = new ArrayList<>();

        if (title != null && !title.isBlank()) {

            sql.append("""
                AND Title LIKE ?
                """);

            parameters.add("%" + title.trim() + "%");
        }

        if (language != null && !language.isBlank()) {

            sql.append("""
                AND Language = ?
                """);

            parameters.add(language.trim());
        }

        if (certificate != null) {

            sql.append("""
                AND Certificate = ?
                """);

            parameters.add(certificate.name());
        }

        if (genre != null && !genre.isBlank()) {

            sql.append("""
                AND Genre LIKE ?
                """);

            parameters.add("%" + genre.trim() + "%");
        }

        sql.append("""
            ORDER BY ReleaseDate
            """);

        return jdbcTemplate.query(
                sql.toString(),
                new MovieListRowMapper(),
                parameters.toArray()
        );
    }

    public MovieDetailsDTO findMovieDetailsById(
            Integer movieId
    ) {

        String movieSql = """
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

        List<MovieDetailsDTO> movies =
                jdbcTemplate.query(
                        movieSql,
                        new MovieDetailsRowMapper(),
                        movieId
                );

        if (movies.isEmpty()) {
            return null;
        }

        MovieDetailsDTO movie = movies.get(0);


        // =========================================================
        // LOAD CAST
        // =========================================================

        String castSql = """
            SELECT Actor
            FROM MovieCast
            WHERE MovieID = ?
            ORDER BY Actor
            """;

        List<String> cast =
                jdbcTemplate.query(
                        castSql,
                        (rs, rowNum) ->
                                rs.getString("Actor"),
                        movieId
                );


        return new MovieDetailsDTO(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getPoster(),
                movie.getLanguage(),
                movie.getGenre(),
                movie.getDuration(),
                movie.getReleaseDate(),
                movie.getImdbRating(),
                movie.getCertificate(),
                movie.getDirector(),
                movie.getDescription(),
                cast
        );
    }
}


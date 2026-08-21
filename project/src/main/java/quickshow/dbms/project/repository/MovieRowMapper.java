package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.model.Certificate;
import quickshow.dbms.project.model.Movie;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MovieRowMapper implements RowMapper<Movie> {

    @Override
    public Movie mapRow(ResultSet rs, int rowNum) throws SQLException {

        Movie movie = new Movie();

        movie.setMovieId(rs.getInt("MovieID"));
        movie.setTitle(rs.getString("Title"));
        movie.setPoster(rs.getString("Poster"));
        movie.setLanguage(rs.getString("Language"));
        movie.setGenre(rs.getString("Genre"));
        movie.setDuration(rs.getInt("Duration"));

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
                    Certificate.valueOf(certificate)
            );
        }

        movie.setDirector(rs.getString("Director"));
        movie.setDescription(rs.getString("Description"));

        return movie;
    }
}
package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.dto.MovieDetailsDTO;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MovieDetailsRowMapper
        implements RowMapper<MovieDetailsDTO> {

    @Override
    public MovieDetailsDTO mapRow(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        return new MovieDetailsDTO(
                rs.getInt("MovieID"),
                rs.getString("Title"),
                rs.getString("Poster"),
                rs.getString("Language"),
                rs.getString("Genre"),
                rs.getInt("Duration"),
                rs.getDate("ReleaseDate") != null
                        ? rs.getDate("ReleaseDate").toLocalDate()
                        : null,
                rs.getBigDecimal("IMDbRating"),
                rs.getString("Certificate"),
                rs.getString("Director"),
                rs.getString("Description"),
                null
        );
    }
}
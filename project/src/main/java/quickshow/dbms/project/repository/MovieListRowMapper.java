package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.dto.MovieListDTO;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MovieListRowMapper implements RowMapper<MovieListDTO> {

    @Override
    public MovieListDTO mapRow(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        return new MovieListDTO(
                rs.getInt("MovieID"),
                rs.getString("Title"),
                rs.getString("Poster"),
                rs.getString("Genre"),
                rs.getBigDecimal("IMDbRating")
        );
    }
}
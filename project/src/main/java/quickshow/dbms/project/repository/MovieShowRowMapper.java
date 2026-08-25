package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.dto.MovieShowDTO;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MovieShowRowMapper
        implements RowMapper<MovieShowDTO> {

    @Override
    public MovieShowDTO mapRow(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        return new MovieShowDTO(
                rs.getInt("ShowID"),
                rs.getString("MovieTitle"),
                rs.getString("Language"),
                rs.getString("ScreenType"),
                rs.getInt("AvailableSeats"),
                rs.getLong("TicketPrice"),
                rs.getTime("ShowTime").toLocalTime()
        );
    }
}
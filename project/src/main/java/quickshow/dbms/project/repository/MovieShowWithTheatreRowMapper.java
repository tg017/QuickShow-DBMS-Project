package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.dto.MovieShowWithTheatreDTO;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MovieShowWithTheatreRowMapper
        implements RowMapper<MovieShowWithTheatreDTO> {

    @Override
    public MovieShowWithTheatreDTO mapRow(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        return new MovieShowWithTheatreDTO(
                rs.getInt("TheatreID"),
                rs.getString("TheatreName"),
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
package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.dto.TheatreShowDTO;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TheatreShowRowMapper
        implements RowMapper<TheatreShowDTO> {

    @Override
    public TheatreShowDTO mapRow(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        return new TheatreShowDTO(
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
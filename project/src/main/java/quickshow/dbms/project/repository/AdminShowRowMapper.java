package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.dto.AdminShowDTO;
import quickshow.dbms.project.model.ShowStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminShowRowMapper
        implements RowMapper<AdminShowDTO> {

    @Override
    public AdminShowDTO mapRow(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        return new AdminShowDTO(
                rs.getInt("ShowID"),

                rs.getDate("ShowDate")
                        .toLocalDate(),

                rs.getTime("ShowTime")
                        .toLocalTime(),

                rs.getLong("TicketPrice"),

                rs.getInt("AvailableSeats"),

                ShowStatus.valueOf(
                        rs.getString("ShowStatus")
                ),

                rs.getInt("MovieID"),

                rs.getInt("ScreenID")
        );
    }
}
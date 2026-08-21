package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.model.Show;
import quickshow.dbms.project.model.ShowStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ShowRowMapper implements RowMapper<Show> {

    @Override
    public Show mapRow(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        Show show = new Show();

        show.setShowId(
                rs.getInt("ShowID")
        );

        if (rs.getDate("ShowDate") != null) {
            show.setShowDate(
                    rs.getDate("ShowDate").toLocalDate()
            );
        }

        if (rs.getTime("ShowTime") != null) {
            show.setShowTime(
                    rs.getTime("ShowTime").toLocalTime()
            );
        }

        show.setTicketPrice(
                rs.getLong("TicketPrice")
        );

        show.setAvailableSeats(
                rs.getInt("AvailableSeats")
        );

        String status =
                rs.getString("ShowStatus");

        if (status != null) {
            show.setShowStatus(
                    ShowStatus.valueOf(status)
            );
        }

        return show;
    }
}
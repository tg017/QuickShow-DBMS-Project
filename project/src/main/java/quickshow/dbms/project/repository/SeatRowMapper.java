package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.model.Seat;
import quickshow.dbms.project.model.SeatId;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SeatRowMapper implements RowMapper<Seat> {

    @Override
    public Seat mapRow(ResultSet rs, int rowNum)
            throws SQLException {

        Seat seat = new Seat();

        SeatId seatId = new SeatId();

        seatId.setScreenId(
                rs.getInt("ScreenID")
        );

        seatId.setSeatId(
                rs.getInt("SeatID")
        );

        seat.setId(seatId);

        seat.setRowNo(
                rs.getString("RowNo")
        );

        seat.setSeatNo(
                rs.getInt("SeatNo")
        );

        return seat;
    }
}
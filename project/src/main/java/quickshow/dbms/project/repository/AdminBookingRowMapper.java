package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.dto.AdminBookingListDTO;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminBookingRowMapper
        implements RowMapper<AdminBookingListDTO> {

    @Override
    public AdminBookingListDTO mapRow(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        return new AdminBookingListDTO(
                rs.getInt("BookingID"),
                rs.getString("MovieName"),
                rs.getString("TheatreName"),
                rs.getString("BookingStatus"),
                rs.getLong("TotalAmount")
        );
    }
}
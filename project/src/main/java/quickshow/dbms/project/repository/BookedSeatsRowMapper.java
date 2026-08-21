package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.model.BookedSeats;
import quickshow.dbms.project.model.BookedSeatsId;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookedSeatsRowMapper
        implements RowMapper<BookedSeats> {

    @Override
    public BookedSeats mapRow(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        BookedSeats bookedSeats =
                new BookedSeats();

        BookedSeatsId id =
                new BookedSeatsId();

        id.setBookingId(
                rs.getInt("BookingID")
        );

        id.setShowId(
                rs.getInt("ShowID")
        );

        id.setScreenId(
                rs.getInt("ScreenID")
        );

        id.setSeatId(
                rs.getInt("SeatID")
        );

        bookedSeats.setId(id);

        /*
         * We deliberately do not populate Booking
         * or ShowSeatAllocation here.
         *
         * Both are relationships and are handled
         * separately when required.
         */

        return bookedSeats;
    }
}
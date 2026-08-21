package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.model.Booking;
import quickshow.dbms.project.model.BookingStatus;
import quickshow.dbms.project.model.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookingRowMapper implements RowMapper<Booking> {

    @Override
    public Booking mapRow(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        Booking booking = new Booking();

        booking.setBookingId(
                rs.getInt("BookingID")
        );

        Customer customer = new Customer();

        customer.setUserId(
                rs.getInt("UserID")
        );

        booking.setCustomer(customer);

        if (rs.getTimestamp("BookingDateTime") != null) {
            booking.setBookingDateTime(
                    rs.getTimestamp("BookingDateTime")
                            .toLocalDateTime()
            );
        }

        String status =
                rs.getString("BookingStatus");

        if (status != null) {
            booking.setBookingStatus(
                    BookingStatus.valueOf(status)
            );
        }

        booking.setTotalAmount(
                rs.getLong("TotalAmount")
        );

        booking.setTotalSeatsCount(
                rs.getInt("TotalSeatsCount")
        );

        return booking;
    }
}
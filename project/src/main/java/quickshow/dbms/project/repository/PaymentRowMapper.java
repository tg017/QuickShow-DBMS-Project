package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.model.Booking;
import quickshow.dbms.project.model.Payment;
import quickshow.dbms.project.model.PaymentMethod;
import quickshow.dbms.project.model.PaymentStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentRowMapper implements RowMapper<Payment> {

    @Override
    public Payment mapRow(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        Payment payment = new Payment();

        payment.setPaymentId(
                rs.getInt("PaymentID")
        );

        PaymentMethod paymentMethod =
                PaymentMethod.valueOf(
                        rs.getString("PaymentMethod")
                );

        payment.setPaymentMethod(
                paymentMethod
        );

        payment.setTransactionId(
                rs.getString("TransactionID")
        );

        payment.setPaymentAmount(
                rs.getLong("PaymentAmount")
        );

        if (rs.getTimestamp("PaymentDateTime") != null) {
            payment.setPaymentDateTime(
                    rs.getTimestamp("PaymentDateTime")
                            .toLocalDateTime()
            );
        }

        PaymentStatus paymentStatus =
                PaymentStatus.valueOf(
                        rs.getString("PaymentStatus")
                );

        payment.setPaymentStatus(
                paymentStatus
        );

        /*
         * We only create a lightweight Booking object
         * containing the BookingID.
         *
         * We do not fetch the complete Booking here.
         */
        Booking booking = new Booking();

        booking.setBookingId(
                rs.getInt("BookingID")
        );

        payment.setBooking(booking);

        return payment;
    }
}
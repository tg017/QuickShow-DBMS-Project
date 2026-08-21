package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import quickshow.dbms.project.model.Payment;
import quickshow.dbms.project.model.PaymentMethod;
import quickshow.dbms.project.model.PaymentStatus;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class PaymentRepository {

    private final JdbcTemplate jdbcTemplate;

    public PaymentRepository(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // =========================================================
    // CREATE
    // =========================================================

    public Payment create(Payment payment) {

        String sql = """
                INSERT INTO Payment
                    (
                        PaymentMethod,
                        TransactionID,
                        PaymentAmount,
                        PaymentDateTime,
                        PaymentStatus,
                        BookingID
                    )
                VALUES
                    (?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder =
                new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement statement =
                    connection.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    );

            statement.setString(
                    1,
                    payment.getPaymentMethod()
                            .name()
            );

            statement.setString(
                    2,
                    payment.getTransactionId()
            );

            statement.setLong(
                    3,
                    payment.getPaymentAmount()
            );

            statement.setObject(
                    4,
                    payment.getPaymentDateTime()
            );

            statement.setString(
                    5,
                    payment.getPaymentStatus()
                            .name()
            );

            statement.setInt(
                    6,
                    payment.getBooking()
                            .getBookingId()
            );

            return statement;

        }, keyHolder);

        Number generatedId =
                keyHolder.getKey();

        if (generatedId == null) {
            throw new IllegalStateException(
                    "PaymentID was not generated"
            );
        }

        payment.setPaymentId(
                generatedId.intValue()
        );

        return payment;
    }


    // =========================================================
    // READ BY ID
    // =========================================================

    public Payment findById(
            Integer paymentId
    ) {

        String sql = """
                SELECT
                    PaymentID,
                    PaymentMethod,
                    TransactionID,
                    PaymentAmount,
                    PaymentDateTime,
                    PaymentStatus,
                    BookingID
                FROM Payment
                WHERE PaymentID = ?
                """;

        List<Payment> payments =
                jdbcTemplate.query(
                        sql,
                        new PaymentRowMapper(),
                        paymentId
                );

        if (payments.isEmpty()) {
            return null;
        }

        return payments.get(0);
    }


    // =========================================================
    // READ ALL
    // =========================================================

    public List<Payment> findAll() {

        String sql = """
                SELECT
                    PaymentID,
                    PaymentMethod,
                    TransactionID,
                    PaymentAmount,
                    PaymentDateTime,
                    PaymentStatus,
                    BookingID
                FROM Payment
                ORDER BY PaymentID
                """;

        return jdbcTemplate.query(
                sql,
                new PaymentRowMapper()
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    public int update(Payment payment) {

        String sql = """
                UPDATE Payment
                SET
                    PaymentMethod = ?,
                    TransactionID = ?,
                    PaymentAmount = ?,
                    PaymentDateTime = ?,
                    PaymentStatus = ?,
                    BookingID = ?
                WHERE PaymentID = ?
                """;

        return jdbcTemplate.update(
                sql,
                payment.getPaymentMethod()
                        .name(),
                payment.getTransactionId(),
                payment.getPaymentAmount(),
                payment.getPaymentDateTime(),
                payment.getPaymentStatus()
                        .name(),
                payment.getBooking()
                        .getBookingId(),
                payment.getPaymentId()
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    public int deleteById(
            Integer paymentId
    ) {

        String sql = """
                DELETE FROM Payment
                WHERE PaymentID = ?
                """;

        return jdbcTemplate.update(
                sql,
                paymentId
        );
    }


    // =========================================================
    // EXISTS
    // =========================================================

    public boolean existsById(
            Integer paymentId
    ) {

        String sql = """
                SELECT COUNT(*)
                FROM Payment
                WHERE PaymentID = ?
                """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        paymentId
                );

        return count != null && count > 0;
    }


    // =========================================================
    // FIND BY BOOKING
    // =========================================================

    public List<Payment> findByBookingId(
            Integer bookingId
    ) {

        String sql = """
                SELECT
                    PaymentID,
                    PaymentMethod,
                    TransactionID,
                    PaymentAmount,
                    PaymentDateTime,
                    PaymentStatus,
                    BookingID
                FROM Payment
                WHERE BookingID = ?
                ORDER BY PaymentID
                """;

        return jdbcTemplate.query(
                sql,
                new PaymentRowMapper(),
                bookingId
        );
    }


    // =========================================================
    // FIND BY TRANSACTION ID
    // =========================================================

    public Payment findByTransactionId(
            String transactionId
    ) {

        String sql = """
                SELECT
                    PaymentID,
                    PaymentMethod,
                    TransactionID,
                    PaymentAmount,
                    PaymentDateTime,
                    PaymentStatus,
                    BookingID
                FROM Payment
                WHERE TransactionID = ?
                """;

        List<Payment> payments =
                jdbcTemplate.query(
                        sql,
                        new PaymentRowMapper(),
                        transactionId
                );

        if (payments.isEmpty()) {
            return null;
        }

        return payments.get(0);
    }


    // =========================================================
    // FIND BY STATUS
    // =========================================================

    public List<Payment> findByStatus(
            PaymentStatus status
    ) {

        String sql = """
                SELECT
                    PaymentID,
                    PaymentMethod,
                    TransactionID,
                    PaymentAmount,
                    PaymentDateTime,
                    PaymentStatus,
                    BookingID
                FROM Payment
                WHERE PaymentStatus = ?
                ORDER BY PaymentID
                """;

        return jdbcTemplate.query(
                sql,
                new PaymentRowMapper(),
                status.name()
        );
    }


    // =========================================================
    // FIND BY PAYMENT METHOD
    // =========================================================

    public List<Payment> findByPaymentMethod(
            PaymentMethod paymentMethod
    ) {

        String sql = """
                SELECT
                    PaymentID,
                    PaymentMethod,
                    TransactionID,
                    PaymentAmount,
                    PaymentDateTime,
                    PaymentStatus,
                    BookingID
                FROM Payment
                WHERE PaymentMethod = ?
                ORDER BY PaymentID
                """;

        return jdbcTemplate.query(
                sql,
                new PaymentRowMapper(),
                paymentMethod.name()
        );
    }
}
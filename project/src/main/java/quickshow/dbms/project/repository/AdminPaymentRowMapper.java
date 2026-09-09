package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.dto.AdminPaymentDTO;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminPaymentRowMapper
        implements RowMapper<AdminPaymentDTO> {

    @Override
    public AdminPaymentDTO mapRow(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        return new AdminPaymentDTO(
                rs.getInt("PaymentID"),

                rs.getString("PaymentMethod"),

                rs.getLong("PaymentAmount"),

                rs.getString("TransactionID"),

                rs.getTimestamp("PaymentDateTime")
                        .toLocalDateTime(),

                rs.getString("PaymentStatus")
        );
    }
}
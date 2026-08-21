package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.model.CustomerEmail;
import quickshow.dbms.project.model.CustomerEmailId;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerEmailRowMapper
        implements RowMapper<CustomerEmail> {

    @Override
    public CustomerEmail mapRow(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        CustomerEmail customerEmail =
                new CustomerEmail();

        CustomerEmailId id =
                new CustomerEmailId();

        id.setUserId(
                rs.getInt("UserID")
        );

        id.setEmail(
                rs.getString("Email")
        );

        customerEmail.setId(id);

        return customerEmail;
    }
}
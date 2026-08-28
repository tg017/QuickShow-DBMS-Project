package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.model.Admin;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminRowMapper implements RowMapper<Admin> {

    @Override
    public Admin mapRow(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        Admin admin = new Admin();

        admin.setAdminId(
                rs.getInt("AdminID")
        );

        admin.setFirstName(
                rs.getString("FirstName")
        );

        admin.setMiddleName(
                rs.getString("MiddleName")
        );

        admin.setLastName(
                rs.getString("LastName")
        );

        admin.setRole(
                rs.getString("Role")
        );

        admin.setPassword(
                rs.getString("Password")
        );

        admin.setEmail(
                rs.getString("Email")
        );

        return admin;
    }
}
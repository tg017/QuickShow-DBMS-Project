package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import quickshow.dbms.project.model.Admin;

import java.util.List;

@Repository
public class AdminRepository {

    private final JdbcTemplate jdbcTemplate;

    public AdminRepository(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // =========================================================
    // FIND ADMIN BY EMAIL
    // =========================================================

    public Admin findByEmail(String email) {

        String sql = """
                SELECT
                    AdminID,
                    FirstName,
                    MiddleName,
                    LastName,
                    Role,
                    Password,
                    Email
                FROM Admin
                WHERE Email = ?
                """;

        List<Admin> admins =
                jdbcTemplate.query(
                        sql,
                        new AdminRowMapper(),
                        email
                );

        if (admins.isEmpty()) {
            return null;
        }

        return admins.get(0);
    }


    // =========================================================
    // CREATE ADMIN
    // =========================================================

    public int createAdmin(
            String firstName,
            String middleName,
            String lastName,
            String role,
            String password,
            String email
    ) {

        String sql = """
                INSERT INTO Admin
                (
                    FirstName,
                    MiddleName,
                    LastName,
                    Role,
                    Password,
                    Email
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                firstName,
                middleName,
                lastName,
                role,
                password,
                email
        );
    }
}
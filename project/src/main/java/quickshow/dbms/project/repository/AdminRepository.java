package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import quickshow.dbms.project.model.Admin;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class AdminRepository {

    private final JdbcTemplate jdbcTemplate;

    public AdminRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // =========================================================
    // CREATE
    // =========================================================

    public Admin create(Admin admin) {

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
                    admin.getFirstName()
            );

            statement.setString(
                    2,
                    admin.getMiddleName()
            );

            statement.setString(
                    3,
                    admin.getLastName()
            );

            statement.setString(
                    4,
                    admin.getRole()
            );

            statement.setString(
                    5,
                    admin.getPassword()
            );

            statement.setString(
                    6,
                    admin.getEmail()
            );

            return statement;

        }, keyHolder);

        Number generatedId =
                keyHolder.getKey();

        if (generatedId == null) {
            throw new IllegalStateException(
                    "AdminID was not generated"
            );
        }

        admin.setAdminId(
                generatedId.intValue()
        );

        return admin;
    }


    // =========================================================
    // READ BY ID
    // =========================================================

    public Admin findById(
            Integer adminId
    ) {

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
                WHERE AdminID = ?
                """;

        List<Admin> admins =
                jdbcTemplate.query(
                        sql,
                        new AdminRowMapper(),
                        adminId
                );

        if (admins.isEmpty()) {
            return null;
        }

        return admins.get(0);
    }


    // =========================================================
    // READ ALL
    // =========================================================

    public List<Admin> findAll() {

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
                ORDER BY AdminID
                """;

        return jdbcTemplate.query(
                sql,
                new AdminRowMapper()
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    public int update(Admin admin) {

        String sql = """
                UPDATE Admin
                SET
                    FirstName = ?,
                    MiddleName = ?,
                    LastName = ?,
                    Role = ?,
                    Password = ?,
                    Email = ?
                WHERE AdminID = ?
                """;

        return jdbcTemplate.update(
                sql,
                admin.getFirstName(),
                admin.getMiddleName(),
                admin.getLastName(),
                admin.getRole(),
                admin.getPassword(),
                admin.getEmail(),
                admin.getAdminId()
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    public int deleteById(
            Integer adminId
    ) {

        String sql = """
                DELETE FROM Admin
                WHERE AdminID = ?
                """;

        return jdbcTemplate.update(
                sql,
                adminId
        );
    }


    // =========================================================
    // EXISTS
    // =========================================================

    public boolean existsById(
            Integer adminId
    ) {

        String sql = """
                SELECT COUNT(*)
                FROM Admin
                WHERE AdminID = ?
                """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        adminId
                );

        return count != null && count > 0;
    }


    // =========================================================
    // FIND BY EMAIL
    // =========================================================

    public List<Admin> findByEmail(
            String email
    ) {

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
                ORDER BY AdminID
                """;

        return jdbcTemplate.query(
                sql,
                new AdminRowMapper(),
                email
        );
    }


    // =========================================================
    // FIND BY ROLE
    // =========================================================

    public List<Admin> findByRole(
            String role
    ) {

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
                WHERE Role = ?
                ORDER BY AdminID
                """;

        return jdbcTemplate.query(
                sql,
                new AdminRowMapper(),
                role
        );
    }
}
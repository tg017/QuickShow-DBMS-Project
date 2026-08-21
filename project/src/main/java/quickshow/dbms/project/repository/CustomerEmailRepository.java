package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import quickshow.dbms.project.model.CustomerEmail;
import quickshow.dbms.project.model.CustomerEmailId;

import java.util.List;

@Repository
public class CustomerEmailRepository {

    private final JdbcTemplate jdbcTemplate;

    public CustomerEmailRepository(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // =========================================================
    // CREATE
    // =========================================================

    public CustomerEmail create(
            CustomerEmail customerEmail
    ) {

        String sql = """
                INSERT INTO CustomerEmails
                    (
                        UserID,
                        Email
                    )
                VALUES
                    (?, ?)
                """;

        jdbcTemplate.update(
                sql,
                customerEmail.getId().getUserId(),
                customerEmail.getId().getEmail()
        );

        return customerEmail;
    }


    // =========================================================
    // READ BY COMPOSITE ID
    // =========================================================

    public CustomerEmail findById(
            CustomerEmailId id
    ) {

        String sql = """
                SELECT
                    UserID,
                    Email
                FROM CustomerEmails
                WHERE UserID = ?
                  AND Email = ?
                """;

        List<CustomerEmail> emails =
                jdbcTemplate.query(
                        sql,
                        new CustomerEmailRowMapper(),
                        id.getUserId(),
                        id.getEmail()
                );

        if (emails.isEmpty()) {
            return null;
        }

        return emails.get(0);
    }


    // =========================================================
    // READ ALL
    // =========================================================

    public List<CustomerEmail> findAll() {

        String sql = """
                SELECT
                    UserID,
                    Email
                FROM CustomerEmails
                ORDER BY UserID, Email
                """;

        return jdbcTemplate.query(
                sql,
                new CustomerEmailRowMapper()
        );
    }


    // =========================================================
    // FIND ALL EMAILS OF A CUSTOMER
    // =========================================================

    public List<CustomerEmail> findByCustomerId(
            Integer userId
    ) {

        String sql = """
                SELECT
                    UserID,
                    Email
                FROM CustomerEmails
                WHERE UserID = ?
                ORDER BY Email
                """;

        return jdbcTemplate.query(
                sql,
                new CustomerEmailRowMapper(),
                userId
        );
    }


    // =========================================================
    // FIND BY EMAIL
    // =========================================================

    public CustomerEmail findByEmail(
            String email
    ) {

        String sql = """
                SELECT
                    UserID,
                    Email
                FROM CustomerEmails
                WHERE Email = ?
                """;

        List<CustomerEmail> emails =
                jdbcTemplate.query(
                        sql,
                        new CustomerEmailRowMapper(),
                        email
                );

        if (emails.isEmpty()) {
            return null;
        }

        return emails.get(0);
    }


    // =========================================================
    // UPDATE EMAIL
    // =========================================================

    public int updateEmail(
            CustomerEmailId oldId,
            String newEmail
    ) {

        String sql = """
                UPDATE CustomerEmails
                SET Email = ?
                WHERE UserID = ?
                  AND Email = ?
                """;

        return jdbcTemplate.update(
                sql,
                newEmail,
                oldId.getUserId(),
                oldId.getEmail()
        );
    }


    // =========================================================
    // DELETE BY COMPOSITE ID
    // =========================================================

    public int deleteById(
            CustomerEmailId id
    ) {

        String sql = """
                DELETE FROM CustomerEmails
                WHERE UserID = ?
                  AND Email = ?
                """;

        return jdbcTemplate.update(
                sql,
                id.getUserId(),
                id.getEmail()
        );
    }


    // =========================================================
    // DELETE ALL EMAILS OF CUSTOMER
    // =========================================================

    public int deleteByCustomerId(
            Integer userId
    ) {

        String sql = """
                DELETE FROM CustomerEmails
                WHERE UserID = ?
                """;

        return jdbcTemplate.update(
                sql,
                userId
        );
    }


    // =========================================================
    // EXISTS
    // =========================================================

    public boolean existsById(
            CustomerEmailId id
    ) {

        String sql = """
                SELECT COUNT(*)
                FROM CustomerEmails
                WHERE UserID = ?
                  AND Email = ?
                """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        id.getUserId(),
                        id.getEmail()
                );

        return count != null && count > 0;
    }
}
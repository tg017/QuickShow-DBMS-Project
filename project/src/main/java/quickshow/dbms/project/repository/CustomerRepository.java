package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import quickshow.dbms.project.dto.CustomerProfileDTO;
import quickshow.dbms.project.dto.RegisterRequestDTO;
import quickshow.dbms.project.repository.data.CustomerLoginData;
import quickshow.dbms.project.repository.data.CustomerProfileData;

import java.util.List;

@Repository
public class CustomerRepository {

    private final JdbcTemplate jdbcTemplate;

    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer createCustomer(RegisterRequestDTO request) {

        String sql = """
                INSERT INTO Customer
                (
                    FirstName,
                    MiddleName,
                    LastName,
                    DOB,
                    Gender,
                    PhoneNo,
                    Password,
                    HouseNo,
                    Street,
                    Area,
                    City,
                    State,
                    PinCode
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                request.getFirstName(),
                request.getMiddleName(),
                request.getLastName(),
                request.getDob(),
                request.getGender(),
                request.getPhoneNo(),
                request.getPassword(),
                request.getHouseNo(),
                request.getStreet(),
                request.getArea(),
                request.getCity(),
                request.getState(),
                request.getPinCode()
        );

        return jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Integer.class
        );
    }

    public void createCustomerEmail(Integer userId, String email) {

        String sql = """
                INSERT INTO CustomerEmails
                (
                    UserID,
                    Email
                )
                VALUES (?, ?)
                """;

        jdbcTemplate.update(
                sql,
                userId,
                email
        );
    }

    public boolean emailExists(String email) {

        String sql = """
                SELECT COUNT(*)
                FROM CustomerEmails
                WHERE Email = ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                email
        );

        return count != null && count > 0;
    }

    public CustomerLoginData findLoginDataByEmail(String email) {

        String sql = """
            SELECT
                c.UserId,
                c.FirstName,
                c.LastName,
                ce.Email,
                c.Password
            FROM Customer c
            JOIN CustomerEmails ce
                ON c.UserId = ce.UserID
            WHERE ce.Email = ?
            """;

        List<CustomerLoginData> result =
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) ->
                                new CustomerLoginData(
                                        rs.getInt("UserId"),
                                        rs.getString("FirstName"),
                                        rs.getString("LastName"),
                                        rs.getString("Email"),
                                        rs.getString("Password")
                                ),
                        email
                );

        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

    public CustomerProfileData findProfileByEmail(
            String email
    ) {

        String sql = """
            SELECT
                c.UserId,
                c.FirstName,
                c.MiddleName,
                c.LastName,
                ce.Email,
                c.PhoneNo,
                c.City,
                c.State,
                c.PinCode
            FROM Customer c
            JOIN CustomerEmails ce
                ON c.UserId = ce.UserID
            WHERE ce.Email = ?
            """;

        List<CustomerProfileData> result =
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) ->
                                new CustomerProfileData(
                                        rs.getInt("UserId"),
                                        rs.getString("FirstName"),
                                        rs.getString("MiddleName"),
                                        rs.getString("LastName"),
                                        rs.getString("Email"),
                                        rs.getString("PhoneNo"),
                                        rs.getString("City"),
                                        rs.getString("State"),
                                        rs.getString("PinCode")
                                ),
                        email
                );

        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

    public CustomerProfileDTO findProfileById(
            Integer userId
    ) {

        String sql = """
                SELECT
                    c.UserId,
                    c.FirstName,
                    c.MiddleName,
                    c.LastName,
                    c.DOB,
                    c.Gender,
                    c.PhoneNo,
                    ce.Email,
                    c.HouseNo,
                    c.Street,
                    c.Area,
                    c.City,
                    c.State,
                    c.PinCode

                FROM Customer c

                LEFT JOIN CustomerEmails ce
                    ON c.UserId = ce.UserID

                WHERE c.UserId = ?
                """;

        List<CustomerProfileDTO> results =
                jdbcTemplate.query(
                        sql,
                        new CustomerProfileRowMapper(),
                        userId
                );

        if (results.isEmpty()) {
            return null;
        }

        return results.get(0);
    }
}
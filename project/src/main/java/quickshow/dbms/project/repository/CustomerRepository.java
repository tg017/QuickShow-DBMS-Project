package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import quickshow.dbms.project.model.Customer;
import quickshow.dbms.project.model.Gender;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class CustomerRepository {

    private final JdbcTemplate jdbcTemplate;

    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // =========================================================
    // CREATE
    // =========================================================

    public Customer create(Customer customer) {

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
                VALUES
                    (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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
                    customer.getFirstName()
            );

            statement.setString(
                    2,
                    customer.getMiddleName()
            );

            statement.setString(
                    3,
                    customer.getLastName()
            );

            statement.setObject(
                    4,
                    customer.getDob()
            );

            if (customer.getGender() != null) {
                statement.setString(
                        5,
                        customer.getGender().name()
                );
            } else {
                statement.setNull(
                        5,
                        java.sql.Types.VARCHAR
                );
            }

            statement.setString(
                    6,
                    customer.getPhoneNo()
            );

            statement.setString(
                    7,
                    customer.getPassword()
            );

            statement.setString(
                    8,
                    customer.getHouseNo()
            );

            statement.setString(
                    9,
                    customer.getStreet()
            );

            statement.setString(
                    10,
                    customer.getArea()
            );

            statement.setString(
                    11,
                    customer.getCity()
            );

            statement.setString(
                    12,
                    customer.getState()
            );

            statement.setString(
                    13,
                    customer.getPinCode()
            );

            return statement;

        }, keyHolder);

        Number generatedId =
                keyHolder.getKey();

        if (generatedId == null) {
            throw new IllegalStateException(
                    "UserID was not generated"
            );
        }

        customer.setUserId(
                generatedId.intValue()
        );

        return customer;
    }


    // =========================================================
    // READ BY ID
    // =========================================================

    public Customer findById(Integer userId) {

        String sql = """
                SELECT
                    UserID,
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
                FROM Customer
                WHERE UserID = ?
                """;

        List<Customer> customers =
                jdbcTemplate.query(
                        sql,
                        new CustomerRowMapper(),
                        userId
                );

        if (customers.isEmpty()) {
            return null;
        }

        return customers.get(0);
    }


    // =========================================================
    // READ ALL
    // =========================================================

    public List<Customer> findAll() {

        String sql = """
                SELECT
                    UserID,
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
                FROM Customer
                ORDER BY UserID
                """;

        return jdbcTemplate.query(
                sql,
                new CustomerRowMapper()
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    public int update(Customer customer) {

        String sql = """
                UPDATE Customer
                SET
                    FirstName = ?,
                    MiddleName = ?,
                    LastName = ?,
                    DOB = ?,
                    Gender = ?,
                    PhoneNo = ?,
                    Password = ?,
                    HouseNo = ?,
                    Street = ?,
                    Area = ?,
                    City = ?,
                    State = ?,
                    PinCode = ?
                WHERE UserID = ?
                """;

        String gender = null;

        if (customer.getGender() != null) {
            gender =
                    customer.getGender().name();
        }

        return jdbcTemplate.update(
                sql,
                customer.getFirstName(),
                customer.getMiddleName(),
                customer.getLastName(),
                customer.getDob(),
                gender,
                customer.getPhoneNo(),
                customer.getPassword(),
                customer.getHouseNo(),
                customer.getStreet(),
                customer.getArea(),
                customer.getCity(),
                customer.getState(),
                customer.getPinCode(),
                customer.getUserId()
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    public int deleteById(Integer userId) {

        String sql = """
                DELETE FROM Customer
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

    public boolean existsById(Integer userId) {

        String sql = """
                SELECT COUNT(*)
                FROM Customer
                WHERE UserID = ?
                """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        userId
                );

        return count != null && count > 0;
    }


    // =========================================================
    // FIND BY PHONE NUMBER
    // =========================================================

    public List<Customer> findByPhoneNo(
            String phoneNo
    ) {

        String sql = """
                SELECT
                    UserID,
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
                FROM Customer
                WHERE PhoneNo = ?
                ORDER BY UserID
                """;

        return jdbcTemplate.query(
                sql,
                new CustomerRowMapper(),
                phoneNo
        );
    }


    // =========================================================
    // FIND BY CITY
    // =========================================================

    public List<Customer> findByCity(
            String city
    ) {

        String sql = """
                SELECT
                    UserID,
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
                FROM Customer
                WHERE City = ?
                ORDER BY FirstName, LastName
                """;

        return jdbcTemplate.query(
                sql,
                new CustomerRowMapper(),
                city
        );
    }


    // =========================================================
    // FIND BY GENDER
    // =========================================================

    public List<Customer> findByGender(
            Gender gender
    ) {

        String sql = """
                SELECT
                    UserID,
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
                FROM Customer
                WHERE Gender = ?
                ORDER BY UserID
                """;

        return jdbcTemplate.query(
                sql,
                new CustomerRowMapper(),
                gender.name()
        );
    }


    // =========================================================
    // FIND BY EMAIL AND PASSWORD
    // =========================================================

    public Customer findByEmailAndPassword(
            String email,
            String password
    ) {

        String sql = """
                SELECT
                    c.UserID,
                    c.FirstName,
                    c.MiddleName,
                    c.LastName,
                    c.DOB,
                    c.Gender,
                    c.PhoneNo,
                    c.Password,
                    c.HouseNo,
                    c.Street,
                    c.Area,
                    c.City,
                    c.State,
                    c.PinCode
                FROM Customer c
                JOIN CustomerEmails ce
                    ON c.UserID = ce.UserID
                WHERE ce.Email = ?
                  AND c.Password = ?
                """;

        List<Customer> customers =
                jdbcTemplate.query(
                        sql,
                        new CustomerRowMapper(),
                        email,
                        password
                );

        if (customers.isEmpty()) {
            return null;
        }

        return customers.get(0);
    }
}
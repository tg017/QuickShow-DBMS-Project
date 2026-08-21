package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.model.Customer;
import quickshow.dbms.project.model.Gender;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerRowMapper implements RowMapper<Customer> {

    @Override
    public Customer mapRow(ResultSet rs, int rowNum)
            throws SQLException {

        Customer customer = new Customer();

        customer.setUserId(
                rs.getInt("UserID")
        );

        customer.setFirstName(
                rs.getString("FirstName")
        );

        customer.setMiddleName(
                rs.getString("MiddleName")
        );

        customer.setLastName(
                rs.getString("LastName")
        );

        if (rs.getDate("DOB") != null) {
            customer.setDob(
                    rs.getDate("DOB").toLocalDate()
            );
        }

        String gender =
                rs.getString("Gender");

        if (gender != null) {
            customer.setGender(
                    Gender.valueOf(gender)
            );
        }

        customer.setPhoneNo(
                rs.getString("PhoneNo")
        );

        customer.setPassword(
                rs.getString("Password")
        );

        customer.setHouseNo(
                rs.getString("HouseNo")
        );

        customer.setStreet(
                rs.getString("Street")
        );

        customer.setArea(
                rs.getString("Area")
        );

        customer.setCity(
                rs.getString("City")
        );

        customer.setState(
                rs.getString("State")
        );

        customer.setPinCode(
                rs.getString("PinCode")
        );

        return customer;
    }
}
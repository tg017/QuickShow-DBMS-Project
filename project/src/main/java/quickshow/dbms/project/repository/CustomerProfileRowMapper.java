package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.dto.CustomerProfileDTO;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerProfileRowMapper
        implements RowMapper<CustomerProfileDTO> {

    @Override
    public CustomerProfileDTO mapRow(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        CustomerProfileDTO profile =
                new CustomerProfileDTO();

        profile.setUserId(
                rs.getInt("UserId")
        );

        profile.setFirstName(
                rs.getString("FirstName")
        );

        profile.setMiddleName(
                rs.getString("MiddleName")
        );

        profile.setLastName(
                rs.getString("LastName")
        );

        if (rs.getDate("DOB") != null) {
            profile.setDob(
                    rs.getDate("DOB").toLocalDate()
            );
        }

        profile.setGender(
                rs.getString("Gender")
        );

        profile.setPhoneNo(
                rs.getString("PhoneNo")
        );

        profile.setEmail(
                rs.getString("Email")
        );

        profile.setHouseNo(
                rs.getString("HouseNo")
        );

        profile.setStreet(
                rs.getString("Street")
        );

        profile.setArea(
                rs.getString("Area")
        );

        profile.setCity(
                rs.getString("City")
        );

        profile.setState(
                rs.getString("State")
        );

        profile.setPinCode(
                rs.getString("PinCode")
        );

        return profile;
    }
}
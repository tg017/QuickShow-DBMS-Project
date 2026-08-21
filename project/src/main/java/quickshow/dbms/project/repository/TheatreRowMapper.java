package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.model.Theatre;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TheatreRowMapper implements RowMapper<Theatre> {

    @Override
    public Theatre mapRow(ResultSet rs, int rowNum)
            throws SQLException {

        Theatre theatre = new Theatre();

        theatre.setTheatreId(
                rs.getInt("TheatreID")
        );

        theatre.setName(
                rs.getString("Name")
        );

        theatre.setContactNo(
                rs.getString("ContactNo")
        );

        theatre.setBuildingName(
                rs.getString("BuildingName")
        );

        theatre.setStreet(
                rs.getString("Street")
        );

        theatre.setArea(
                rs.getString("Area")
        );

        theatre.setCity(
                rs.getString("City")
        );

        theatre.setState(
                rs.getString("State")
        );

        theatre.setPinCode(
                rs.getString("PinCode")
        );

        return theatre;
    }
}
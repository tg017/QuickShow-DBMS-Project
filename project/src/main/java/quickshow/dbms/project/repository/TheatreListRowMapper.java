package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.dto.TheatreListDTO;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TheatreListRowMapper
        implements RowMapper<TheatreListDTO> {

    @Override
    public TheatreListDTO mapRow(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        return new TheatreListDTO(
                rs.getInt("TheatreID"),
                rs.getString("Name"),
                rs.getString("BuildingName"),
                rs.getString("Street"),
                rs.getString("Area"),
                rs.getString("City"),
                rs.getString("State"),
                rs.getString("PinCode")
        );
    }
}
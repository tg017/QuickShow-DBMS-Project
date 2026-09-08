package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;

import quickshow.dbms.project.dto.AdminScreenDTO;
import quickshow.dbms.project.model.ScreenType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminScreenRowMapper
        implements RowMapper<AdminScreenDTO> {

    @Override
    public AdminScreenDTO mapRow(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        return new AdminScreenDTO(
                rs.getInt("ScreenID"),
                rs.getString("ScreenName"),
                ScreenType.valueOf(
                        rs.getString("ScreenType")
                ),
                rs.getInt("SeatingCapacity"),
                rs.getInt("TheatreID"),
                null
        );
    }
}
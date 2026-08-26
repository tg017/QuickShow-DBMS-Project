package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.dto.SeatDTO;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SeatRowMapper
        implements RowMapper<SeatDTO> {

    @Override
    public SeatDTO mapRow(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        return new SeatDTO(
                rs.getInt("SeatID"),
                rs.getString("RowNo"),
                rs.getInt("SeatNo"),
                rs.getString("Status")
        );
    }
}
package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.model.SeatAllocationStatus;
import quickshow.dbms.project.model.ShowSeatAllocation;
import quickshow.dbms.project.model.ShowSeatAllocationId;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ShowSeatAllocationRowMapper
        implements RowMapper<ShowSeatAllocation> {

    @Override
    public ShowSeatAllocation mapRow(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        ShowSeatAllocation allocation =
                new ShowSeatAllocation();

        ShowSeatAllocationId id =
                new ShowSeatAllocationId();

        id.setShowId(
                rs.getInt("ShowID")
        );

        id.setScreenId(
                rs.getInt("ScreenID")
        );

        id.setSeatId(
                rs.getInt("SeatID")
        );

        allocation.setId(id);

        String status =
                rs.getString("Status");

        if (status != null) {
            allocation.setStatus(
                    SeatAllocationStatus.valueOf(status)
            );
        }

        return allocation;
    }
}
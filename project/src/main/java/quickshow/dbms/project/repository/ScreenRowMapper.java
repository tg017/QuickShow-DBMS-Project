package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.model.Screen;
import quickshow.dbms.project.model.ScreenType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ScreenRowMapper implements RowMapper<Screen> {

    @Override
    public Screen mapRow(ResultSet rs, int rowNum)
            throws SQLException {

        Screen screen = new Screen();

        screen.setScreenId(
                rs.getInt("ScreenID")
        );

        screen.setName(
                rs.getString("ScreenName")
        );

        String screenType =
                rs.getString("ScreenType");

        if (screenType != null) {
            screen.setScreenType(
                    ScreenType.valueOf(screenType)
            );
        }

        screen.setCapacity(
                rs.getInt("SeatingCapacity")
        );

        /*
         * We deliberately do not populate Theatre here.
         *
         * Theatre is a dependent relationship and will be
         * handled separately when required.
         */

        return screen;
    }
}
package org.test.sqltest;

import com.vaadin.ui.Grid;

import java.sql.SQLException;

public class Users extends AbstractCustomGrid {
    public Users(String title) throws SQLException {
        super(title);

        panel.setContent(new Grid(users));
    }

    public Users() throws SQLException {
        this("Users");
    }
}

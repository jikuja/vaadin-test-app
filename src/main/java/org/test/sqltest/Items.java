package org.test.sqltest;

import com.vaadin.ui.Grid;

import java.sql.SQLException;

public class Items extends AbstractCustomGrid {
    public Items(String title) throws SQLException {
        super(title);

        panel.setContent(new Grid(items));
    }

    public Items() throws SQLException {
        this("Items");
    }
}

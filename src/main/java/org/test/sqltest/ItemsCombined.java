package org.test.sqltest;

import com.vaadin.ui.Grid;

import java.sql.SQLException;


public class ItemsCombined extends AbstractCustomGridSqlView {
    public ItemsCombined(String title) throws SQLException {
        super(title);

        // This will not work. SQLContainer requires that table has primary key(s)
        // View does not have => View can not be used us cheap and dirty FreeFormQuery
        // replacement. :/

        // Time to learn how to use FreeFormQuery
        Grid grid = new Grid(items);
        panel.setContent(grid);
    }

    public ItemsCombined() throws SQLException {
        super("SQL table combined");
    }
}

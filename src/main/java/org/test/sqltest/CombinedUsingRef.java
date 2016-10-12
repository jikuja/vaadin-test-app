package org.test.sqltest;

import com.vaadin.ui.Grid;

import java.sql.SQLException;

public class CombinedUsingRef extends AbstractCustomGrid {
    public CombinedUsingRef(String title) throws SQLException {
        super(title);

        items.addReference(users, "USER_ID", "ID");
        Grid grid = new Grid(items);

        // TODO: add some magic here
        // * remove unneeded columns
        // * make "User" column to show user.name
        // * is it even possible with TableQuery?
        // * Is it possible to sort or filter with users.name?

        // Converter, Renderer?

        panel.setContent(grid);
    }

    public CombinedUsingRef() throws SQLException {
        this("Items +  addReference(users, ...)");
    }
}

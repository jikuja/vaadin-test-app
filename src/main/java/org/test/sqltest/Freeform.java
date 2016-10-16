package org.test.sqltest;

import com.vaadin.data.Container;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Panel;
import org.test.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Freeform extends CustomComponent {
    private Panel panel;

    public Freeform(String title) throws SQLException {
        // setup GUI
        panel = new Panel(title);
        setCompositionRoot(panel);

        SQLContainer sqlContainer = getSqlContainer();

        Grid grid = new Grid(sqlContainer);
        panel.setContent(grid);
    }

    public Freeform() throws SQLException {
        this("FreeformQuery");
    }

    // static for testing purposes
    public static SQLContainer getSqlContainer() throws SQLException {
        FreeformQuery query = new FreeformQuery("SELECT * FROM items i LEFT JOIN users u ON i.USER_ID=u.ID", Database.pool, "id");
        //query.setDelegate(new Asdf());
        SQLContainer sqlContainer = new SQLContainer(query);
        return sqlContainer;
    }
}

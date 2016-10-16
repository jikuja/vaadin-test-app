package org.test.sqltest;


import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;
import org.test.Database;

import java.sql.SQLException;

class AbstractCustomGrid extends CustomComponent {
    protected SQLContainer users;
    protected SQLContainer items;
    protected Panel panel;

    public AbstractCustomGrid(String title) throws SQLException {
        // setup GUI
        panel = new Panel(title);
        setCompositionRoot(panel);

        TableQuery itemsTableQuery = new TableQuery("items", Database.pool);
        itemsTableQuery.setVersionColumn("OPTLOCK");

        TableQuery usersTableQuery = new TableQuery("users", Database.pool);
        usersTableQuery.setVersionColumn("OPTLOCK");

        items = new SQLContainer(itemsTableQuery);
        users = new SQLContainer(usersTableQuery);

        // concrete classes adds Grids/Tables into panel
    }
}

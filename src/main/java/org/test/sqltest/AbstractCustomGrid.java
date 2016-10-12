package org.test.sqltest;


import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;
import org.test.Consts;

import java.sql.SQLException;

class AbstractCustomGrid extends CustomComponent {
    protected SQLContainer users;
    protected SQLContainer items;
    protected Panel panel;

    public AbstractCustomGrid(String title) throws SQLException {
        // setup GUI
        panel = new Panel(title);
        setCompositionRoot(panel);

        // setup TableQuerys
        // TODO: pool probably should be shared single instance. fixme
        JDBCConnectionPool pool = new SimpleJDBCConnectionPool(
                Consts.JDBC_DRIVER, // driver
                Consts.JDBC_URL,  // connection url
                Consts.JDBC_USER, Consts.JDBC_PASS,
                2, 5);

        TableQuery itemsTableQuery = new TableQuery("items", pool);
        itemsTableQuery.setVersionColumn("OPTLOCK");

        TableQuery usersTableQuery = new TableQuery("users", pool);
        usersTableQuery.setVersionColumn("OPTLOCK");

        items = new SQLContainer(itemsTableQuery);
        users = new SQLContainer(usersTableQuery);

        // concrete classes adds Grids/Tables into panel
    }
}

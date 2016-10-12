package org.test.sqltest;


import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;
import org.test.Consts;

import java.sql.SQLException;

class AbstractCustomGridSqlView extends CustomComponent {
    protected SQLContainer items;
    protected Panel panel;

    public AbstractCustomGridSqlView(String title) throws SQLException {
        // setup GUI
        panel = new Panel(title);
        setCompositionRoot(panel);

        // setup TableQuerys
        JDBCConnectionPool pool = new SimpleJDBCConnectionPool(
                Consts.JDBC_DRIVER, // driver
                Consts.JDBC_URL,  // connection url
                Consts.JDBC_USER, Consts.JDBC_PASS,
                2, 5);

        // WHY DOES NOT WORK with sqlite?! I have View named combined, please work!
        // BUG?
        TableQuery itemsTableQuery = new TableQuery("combined", pool);
        //itemsTableQuery.setVersionColumn("OPTLOCK");

        items = new SQLContainer(itemsTableQuery);

        // concrete classes adds Grids/Tables into panel
    }
}

package org.test;

import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;

import javax.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Database {
    public static JDBCConnectionPool pool;
    //public static final String BASEPATH = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();

    public static final String JDBC_DRIVER = "org.hsqldb.jdbc.JDBCDriver";

    // this will create src/main/webapp/hsqldb.db directory when running with jetty. WTF
    // did not create tables, table creating visible in the log
    //public static final String JDBC_URL = "jdbc:hsqldb:file:" + BASEPATH + "/hsqldb.db/vaadin";

    //public static final String JDBC_URL = "jdbc:hsqldb:file:" + "d:/hsqldb.db/vaadin";

    // something really did not work: data was not added
    public static final String JDBC_URL = "jdbc:hsqldb:mem:vaadin";
    public static final  String JDBC_USER = "SA";
    public static final String JDBC_PASS = "";

    private Database() {
    }

    static {
        initDatabase();
    }

    private static void initDatabase() {
        Connection connection;
        boolean createTables = false;

        try {
            pool = new SimpleJDBCConnectionPool(
                    Database.JDBC_DRIVER, // driver
                    Database.JDBC_URL,  // connection url
                    Database.JDBC_USER, Database.JDBC_PASS,
                    2, 5);


            connection = pool.reserveConnection();
            // autocommit for connection is false by default
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            Statement statement = connection.createStatement();
            statement.execute("SELECT * FROM items");
            statement.close();
            connection.commit();
        } catch (SQLException e) {
            // database does not exist. Create it.
            // probably more elegant way to ech would be check if tables exist

            // Create tables
            createTables = true;
        }

        if (createTables) {
            // not working. tables are created but no data inserted.
            try {
                List<String> resources = new ArrayList<>();
                resources.add("/WEB-INF/classes/ddl.sql");
                resources.add("/WEB-INF/classes/sql/PUBLIC_PUBLIC_USERS.sql");
                resources.add("/WEB-INF/classes/sql/PUBLIC_PUBLIC_ITEMS.sql");

                for (String res: resources) {
                    ServletContext context = VaadinServlet.getCurrent().getServletContext();
                    InputStream in = context.getResourceAsStream(res);
                    String sql = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
                    Statement statement = connection.createStatement();
                    statement.execute(sql);
                    statement.close();
                    connection.commit();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        try {
            // wat. What is correct way to close connection?
            //pool.releaseConnection(connection); // breaks things. Should not?
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}

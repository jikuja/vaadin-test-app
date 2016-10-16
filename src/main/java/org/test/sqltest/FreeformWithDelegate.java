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

public class FreeformWithDelegate extends CustomComponent {
    private Panel panel;

    public FreeformWithDelegate(String title) throws SQLException {
        // setup GUI
        panel = new Panel(title);
        setCompositionRoot(panel);

        SQLContainer sqlContainer = getSqlContainer();

        Grid grid = new Grid(sqlContainer);
        grid.header
        panel.setContent(grid);
    }

    public FreeformWithDelegate() throws SQLException {
        this("FreeformQuery");
    }

    // static for testing purposes
    public static SQLContainer getSqlContainer() throws SQLException {
        String q = "SELECT * FROM items i LEFT JOIN users u ON i.USER_ID=u.ID";
        FreeformQuery query = new FreeformQuery(q, Database.pool, "id");
        query.setDelegate(new Asdf(q));
        SQLContainer sqlContainer = new SQLContainer(query);
        return sqlContainer;
    }

    private static class Asdf implements FreeformStatementDelegate {
        private List<Container.Filter> filters;
        private List<OrderBy> orderBys;
        private List<String> fields;
        private String query;

        public Asdf(String query) {
            this.query = query;
        }

        // FreeformStatementDelegate
        @Override
        public StatementHelper getQueryStatement(int offset, int limit) throws UnsupportedOperationException {
            StatementHelper statementHelper = new StatementHelper();
            StringBuilder sb = new StringBuilder(query);
            if (filters != null) {
                sb.append(QueryBuilder.getWhereStringForFilters(filters, statementHelper));
            }

            // TODO: implement ordering
            // see: https://dev.vaadin.com/browser/svn/addons/SQLContainer/trunk/demo/src/com/vaadin/addon/sqlcontainer/demo/DemoFreeformQueryDelegate.java
            // for ref

            if (offset != 0 && limit != 0) {
                sb.append(" LIMIT ").append(limit);
                sb.append(" OFFSET ").append(offset);
            }

            statementHelper.setQueryString(sb.toString());
            return statementHelper;
        }

        @Override
        public StatementHelper getCountStatement() throws UnsupportedOperationException {
            StatementHelper statementHelper = new StatementHelper();
            StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM items i LEFT JOIN users u ON i.USER_ID=u.ID");

            if (filters != null) {
                sb.append(QueryBuilder.getWhereStringForFilters(filters, statementHelper));
            }

            statementHelper.setQueryString(sb.toString());
            return statementHelper;
        }

        @Override
        public StatementHelper getContainsRowQueryStatement(Object... keys) throws UnsupportedOperationException {
            // TODO: what this is actually is?
            throw new UnsupportedOperationException();
        }

        // FreeformQueryDelegate
        @Override
        public String getQueryString(int offset, int limit) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Deprecated method");
        }

        @Override
        public String getCountQuery() throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Deprecated method");
        }

        @Override
        public void setFilters(List<Container.Filter> filters) throws UnsupportedOperationException {
            this.filters = filters;
        }

        @Override
        public void setOrderBy(List<OrderBy> orderBys) throws UnsupportedOperationException {
            if (orderBys != null) {
                // undocumented(?) implementation detail:
                // SQLContainer.<init>() calls getPropertyIds which calls setOrderBy(null) which calls
                // this.setOrderBy(null). If not checked constructor will fail. Same for this.setFilters()
                throw new UnsupportedOperationException("Not implemented");
            }
            //this.orderBys = orderBys;
        }

        @Override
        public int storeRow(Connection conn, RowItem row) throws UnsupportedOperationException, SQLException {
            throw new UnsupportedOperationException("Not implemented");
            // TODO: remember implement locking...
            /*
            Statement statement = conn.createStatement();
            String query;
            if (row.getId() instanceof TemporaryRowId) {
                query = "INSERT";
            } else {
                query = "UPDATE";
            }

            int retval = statement.executeUpdate(query);
            statement.close();
            return retval;
            */
        }

        @Override
        public boolean removeRow(Connection conn, RowItem row) throws UnsupportedOperationException, SQLException {
            PreparedStatement statement = conn.prepareStatement("DELETE FROM users WHERE id=?");
            statement.setInt(1, (Integer)row.getItemProperty("id").getValue());
            int retval = statement.executeUpdate();
            statement.close();
            return retval == 1;
        }

        @Override
        public String getContainsRowQueryString(Object... keys) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Deprecated method");
        }
    }
}

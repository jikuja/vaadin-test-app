package org.test.sqltest;

import com.vaadin.data.Container;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.DefaultSQLGenerator;
import com.vaadin.data.util.sqlcontainer.query.generator.SQLGenerator;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Panel;
import org.test.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class FreeformWithDelegate extends CustomComponent {
    private Panel panel;

    public FreeformWithDelegate(String title) throws SQLException {
        // setup GUI
        panel = new Panel(title);
        setCompositionRoot(panel);

        SQLContainer sqlContainer = getSqlContainer();

        Grid grid = new Grid(sqlContainer);
        sqlContainer.rollback();
        panel.setContent(grid);
    }

    public FreeformWithDelegate() throws SQLException {
        this("FreeformQuery");
    }

    // static for testing purposes
    public static SQLContainer getSqlContainer() throws SQLException {
        String q = "SELECT * FROM items i LEFT JOIN users u ON i.USER_ID=u.ID";

        // this query string is not really used when using deletages but cannot be null or empty
        FreeformQuery query = new FreeformQuery(q, Database.pool, "id");
        query.setDelegate(new Asdf(q));
        SQLContainer sqlContainer = new SQLContainer(query);
        return sqlContainer;
    }

    /*
     * Default SQLGenerator offers nice methods but only for one table.
     * * Extract some usable impelmentations here
     */
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

            if (orderBys != null) {
                for (OrderBy o: orderBys) {
                    generateOrderBy(sb, o, orderBys.indexOf(o) == 0);
                }
            }

            if (limit != 0) {
                sb.append(" LIMIT ").append(limit);
                sb.append(" OFFSET ").append(offset);
            }

            statementHelper.setQueryString(sb.toString());
            System.out.println("getQueryStatement: " + sb.toString());
            return statementHelper;
        }

        @Override
        public StatementHelper getCountStatement() throws UnsupportedOperationException {
            StatementHelper statementHelper = new StatementHelper();
            StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM items");

            if (filters != null) {
                sb.append(QueryBuilder.getWhereStringForFilters(filters, statementHelper));
            }

            statementHelper.setQueryString(sb.toString());
            return statementHelper;
        }

        @Override
        public StatementHelper getContainsRowQueryStatement(Object... keys) throws UnsupportedOperationException {
            // TODO: needed?
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
                // undocumented(?) implementation detail:
                // SQLContainer.<init>() calls getPropertyIds which calls setOrderBy(null) which calls
                // this.setOrderBy(null). If not checked constructor will fail. Same for this.setFilters()
            this.orderBys = orderBys;
        }

        @Override
        public int storeRow(Connection conn, RowItem row) throws UnsupportedOperationException, SQLException {
            PreparedStatement statement = null;
            if (row.getId() instanceof TemporaryRowId) {
                // insert
                statement = conn.prepareStatement("INSERT INTO ITEMS VALUES ?, ?, ?, ?, ?");
                setRowValues(statement, row);
            } else {
                // update
                statement = conn.prepareStatement("UPDATE ITEMS SET TITLE = ?, DESCRIPTION = ?, USER_ID = ?, " +
                        "LAT = ?, LONG = ? WHERE id=?");
                setRowValues(statement, row);
                statement.setInt(6, (Integer) row.getItemProperty("ID").getValue());
            }

            int retval = statement.executeUpdate();
            statement.close();
            return retval;
        }

        protected void setRowValues(PreparedStatement s, RowItem row) throws SQLException {
            s.setString(1, (String) row.getItemProperty("TITLE").getValue());
            s.setString(2, (String) row.getItemProperty("DESCRIPTION").getValue());
            s.setInt(3, (Integer) row.getItemProperty("USER_ID").getValue());
            s.setDouble(4, (Double) row.getItemProperty("LAT").getValue());
            s.setDouble(5, (Double) row.getItemProperty("LONG").getValue());
        }

        @Override
        public boolean removeRow(Connection conn, RowItem row) throws UnsupportedOperationException, SQLException {
            // Note: no locking support
            PreparedStatement statement = conn.prepareStatement("DELETE FROM users WHERE ID=?");
            statement.setInt(1, (Integer)row.getItemProperty("id").getValue()); // fixme id or ID
            int retval = statement.executeUpdate();
            statement.close();
            return retval == 1;
        }

        @Override
        public String getContainsRowQueryString(Object... keys) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Deprecated method");
        }

        // Source: DefaultSQLGeneretor.generateOrderBy
        // changed to use StringBuilder
        protected StringBuilder generateOrderBy(StringBuilder sb, OrderBy o, boolean firstOrderBy) {
            if (firstOrderBy) {
                sb.append(" ORDER BY ");
            } else {
                sb.append(", ");
            }
            sb.append(QueryBuilder.quote(o.getColumn()));
            if (o.isAscending()) {
                sb.append(" ASC");
            } else {
                sb.append(" DESC");
            }
            return sb;
        }
    }
}

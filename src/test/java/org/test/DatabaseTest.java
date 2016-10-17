package org.test;

import com.vaadin.data.Item;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Table;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.test.sqltest.Freeform;
import org.test.sqltest.FreeformWithDelegate;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/*
 * Note: requires -noverify in JVM args
 */
@PrepareForTest({VaadinServlet.class, Servlet.class})
public class DatabaseTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void setup() throws Exception {
        // mock all static to fail by default
        PowerMockito.mockStatic(VaadinServlet.class, new FailAnswer());

        // create mockup object for VaadinServlet.getCurrent
        //VaadinServlet mockedServletInstance = Mockito.mock(VaadinServlet.class, new FailAnswer());
        VaadinServlet mockedServletInstance = Mockito.spy(VaadinServlet.class);

        // return VaadinServlet instance.
        PowerMockito.doReturn(mockedServletInstance).when(VaadinServlet.class, "getCurrent");

        //ServletContext mockedServletContextInstance = Mockito.mock(ServletContext.class, new FailAnswer());
        ServletContext mockedServletContextInstance = Mockito.spy(ServletContext.class);
        Mockito.doReturn(getIS("ddl.sql")).when(mockedServletContextInstance).getResourceAsStream("/WEB-INF/classes/ddl.sql");
        Mockito.doReturn(getIS("sql/PUBLIC_PUBLIC_USERS.sql")).when(mockedServletContextInstance).getResourceAsStream("/WEB-INF/classes/sql/PUBLIC_PUBLIC_USERS.sql");
        Mockito.doReturn(getIS("sql/PUBLIC_PUBLIC_ITEMS.sql")).when(mockedServletContextInstance).getResourceAsStream("/WEB-INF/classes/sql/PUBLIC_PUBLIC_ITEMS.sql");

        Mockito.doReturn(mockedServletContextInstance).when(mockedServletInstance).getServletContext();

    }

    private InputStream getIS(String s) {
        InputStream is = null;
        //InputStream is = getClass().getClassLoader().getResourceAsStream(s.replace("/WEB-INF/classes/", ""));
        try {
            is = new FileInputStream("d:/projects/vaadin/vaadin-test-app/src/main/resources/" + s);
        } catch (Exception e) {
            fail(e.toString());
        }
        assertNotNull(is);
        return is;
    }

    @Test
    public void foo1() throws Exception {
        TableQuery itemsTableQuery = new TableQuery("items", Database.pool);
        itemsTableQuery.setVersionColumn("OPTLOCK");

        TableQuery usersTableQuery = new TableQuery("users", Database.pool);
        usersTableQuery.setVersionColumn("OPTLOCK");

        SQLContainer items = new SQLContainer(itemsTableQuery);
        SQLContainer users = new SQLContainer(usersTableQuery);

        System.out.println(items.size());
        // note. hsqldb converts identifiers to uppercase?
        //items.addContainerFilter("DESCRIPTION", "asdf", true, false);
        items.addContainerFilter(new SimpleStringFilter("DESCRIPTION", "asdf", true, false));
        System.out.println(items.size());
    }

    @Test
    public void filtering() throws Exception {
        SQLContainer c = Freeform.getSqlContainer();
        assertTrue(c.size() > 0);
        assertTrue(c.size() == 5);
        c.addContainerFilter("DESCRIPTION", "asdf", true, false);
        // Note: FreeformQuery without delegate silently ignores filtering and sorting
        assertTrue(c.size() == 5);

    }

    @Test
    public void filteringWithDelegate() throws Exception {
        SQLContainer c = FreeformWithDelegate.getSqlContainer();
        assertTrue(c.size() == 5);
        c.addContainerFilter("DESCRIPTION", "asdf", true, false);
        assertTrue(c.size() == 1);
    }

    @Test
    public void sortWithDelegate() throws Exception {
        SQLContainer c = FreeformWithDelegate.getSqlContainer();
        assertTrue(c.size() == 5);
        Hack.hack();
        c.sort(new Object[] {"DESCRIPTION"}, new boolean[] {true} );
        String s = (String) c.getItem(c.firstItemId()).getItemProperty("DESCRIPTION").getValue();
        assertTrue(s.startsWith("asdf"));
        s = (String) c.getItem(c.lastItemId()).getItemProperty("DESCRIPTION").getValue();
        assertTrue(s.startsWith("zzzz"));
    }

    @Ignore
    @Test
    public void lockingWithDelegate() throws Exception {
        Hack.hack();
        fail();
        SQLContainer c = FreeformWithDelegate.getSqlContainer();
        // get something from c

        // get copy of c and edit it and save

        // edit and save original c. Should throw an error
    }

    @Test
    public void deleteWithDelegate() throws Exception {
        SQLContainer c = FreeformWithDelegate.getSqlContainer();
        assertTrue(c.size() == 5);
        Object itemid = c.firstItemId();
        c.removeItem(itemid);
        assertTrue(c.size() == 4);
    }

    @Test
    public void insertWithDelegate() throws Exception {
        SQLContainer c = FreeformWithDelegate.getSqlContainer();
        assertTrue(c.size() == 5);

        Table table = new Table("", c);
        Object o = table.addItem();
        Item i = table.getItem(o);
        i.getItemProperty("TITLE").setValue("foo");
        table.commit();
        assertTrue(c.size() == 6);
    }

    @Test
    public void updateWithDelegate() throws Exception {
        SQLContainer c = FreeformWithDelegate.getSqlContainer();
        assertTrue(c.size() == 5);
        Object itemid = c.firstItemId();
        Item i = c.getItem(itemid);
        i.getItemProperty("TITLE").setValue("bbbb");
        c.commit();
        assertTrue(c.size() == 5);
    }
}

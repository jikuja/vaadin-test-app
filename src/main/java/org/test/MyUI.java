package org.test;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import org.test.sqltest.CombinedUsingRef;
import org.test.sqltest.FreeformWithDelegate;
import org.test.sqltest.Items;
import org.test.sqltest.Users;
import org.vaadin.addon.leaflet.*;
import org.vaadin.addon.leaflet.shared.Point;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {
    private LMap lmap;
    private boolean ladding = false;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        setContent(tabSheet);

        /* ********************************
         * setup leaflet
         **********************************/
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setHeightUndefined();
        verticalLayout.addComponent(horizontalLayout);

        Panel p1 = new Panel(); // I had to wrap LMap with Panel :/
        lmap = new LMap();
        lmap.setCenter(new Point(60.440963, 22.25122), 14.0);

        p1.setContent(lmap);
        verticalLayout.addComponent(p1);
        lmap.setSizeFull();
        p1.setSizeFull();
        verticalLayout.setExpandRatio(p1, 2);

        Button b = new Button("Add POI");
        b.addClickListener(event -> {
            ladding = !ladding;
            Notification.show("foo", ladding ? "Adding points" : "Not adding piints", Notification.Type.HUMANIZED_MESSAGE);
        });

        LeafletClickListener listener = new LeafletClickListener() {
            @Override
            public void onClick(LeafletClickEvent event) {
                Point p = event.getPoint();
                if (p != null && ladding) {
                    Notification.show("foo", "Clicked map:" + p.getLat() + ", " + p.getLon(), Notification.Type.HUMANIZED_MESSAGE);
                }
            }
        };

        LMarker lMarker = new LMarker(new Point(60.440963, 22.25122));
        lMarker.setTitle("Center of zoom");
        lMarker.setPopup("Center of to default area");
        lmap.addComponent(lMarker);

        lmap.addClickListener(listener);

        // Add some layer. Only one of these will be visible and will be load tiles
        LTileLayer osm = new LOpenStreetMapLayer(); // ready to use OSM layer!
        lmap.addBaseLayer(osm, "OSM");
        osm.setActive(false);

        LTileLayer perus = new LTileLayer("http://tiles.kartat.kapsi.fi/peruskartta/{z}/{x}/{y}.jpg");
        perus.setAttributionString("Map data © <a href=\"http://www.maanmittauslaitos.fi/avoimen-tietoaineiston-cc-40-lisenssi\"> MML</a>");
        lmap.addBaseLayer(perus, "perus");
        perus.setActive(false);

        LTileLayer tausta = new LTileLayer("http://tiles.kartat.kapsi.fi/taustakartta/{z}/{x}/{y}.jpg");
        tausta.setAttributionString("Map data © <a href=\"http://www.maanmittauslaitos.fi/avoimen-tietoaineiston-cc-40-lisenssi\"> MML</a>");
        lmap.addBaseLayer(tausta, "tausta");
        tausta.setActive(false);

        LTileLayer orto = new LTileLayer("http://tiles.kartat.kapsi.fi/ortokuva/{z}/{x}/{y}.jpg");
        orto.setAttributionString("Map data © <a href=\"http://www.maanmittauslaitos.fi/avoimen-tietoaineiston-cc-40-lisenssi\"> MML</a>");
        lmap.addBaseLayer(orto, "orto");
        orto.setActive(false);

        LWmsLayer wmsok = new LWmsLayer();
        wmsok.setUrl("http://opaskartta.turku.fi/TeklaOGCWeb/WMS.ashx");
        wmsok.setTransparent(true);
        wmsok.setLayers("Opaskartta");
        wmsok.setAttributionString("(C) Turun kaupunki");
        lmap.addBaseLayer(wmsok, "asdf");
        wmsok.setActive(true);

        // Add overlay layer top of the map. Multiple layers can be added
        LWmsLayer wms = new LWmsLayer();
        wms.setUrl("http://opaskartta.turku.fi/TeklaOGCWeb/WMS.ashx");
        wms.setTransparent(true);
        wms.setLayers("Opaskartta_pyoratiet");
        wms.setAttributionString("(C) Turun kaupunki");
        lmap.addOverlay(wms, "asdf");


        horizontalLayout.addComponent(b);
        horizontalLayout.addComponent(new Button("FreeformWithDelegate")); // just other button for visual thing
        tabSheet.addTab(verticalLayout, "Leaflet");

        /* *******
         * sql experimentation
         */

        GridLayout grids = new GridLayout();
        try {
            grids.addComponent(new Users());
            grids.addComponent(new Items());
            grids.addComponent(new CombinedUsingRef());
            //grids.addComponent(new ItemsCombined()); // does not work. See class for comments
            grids.addComponent(new FreeformWithDelegate());
        } catch (Exception e) {
            // I don't care. I just want to see error if something fails
            throw new RuntimeException(e);
        }

        // TODO: try filtering and ordering
        //       select columns we actually want
        // make database location relative with $CWD


        // future TODO>
        // add form to add / edit data
        // check how to commit data


        // TODO: add table and then
        // http://stackoverflow.com/questions/15972920/vaadin-sqlcontainer-reference-how-to-implement-foreign-key-relation

        tabSheet.addTab(grids, "Database");
        tabSheet.setSelectedTab(1);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}

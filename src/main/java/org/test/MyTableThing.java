package org.test;

import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.ui.*;
import org.test.sqltest.Freeform;

public class MyTableThing extends CustomComponent {
    HorizontalLayout horizontalLayout = new HorizontalLayout();

    VerticalLayout leftSide = new VerticalLayout();
    SQLContainer gridDs;
    Grid grid;
    HorizontalLayout buttons = new HorizontalLayout();
    Button edit = new Button("Edit");
    Button delete = new Button("Delete");
    Button newB = new Button("New");
    MyFormThing form;

    ///*
    public MyTableThing() {
        setCompositionRoot(horizontalLayout);

        // setup grid
        horizontalLayout.addComponent(leftSide);
        horizontalLayout.setExpandRatio(leftSide, 1);
        leftSide.setSizeFull();
        leftSide.setWidth(800, Unit.PIXELS); //why do I need to set this also?!
        leftSide.setHeight(300, Unit.PIXELS);



        try {
            gridDs = Freeform.getSqlContainer();
            grid = new Grid(gridDs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // setup grid headers
        grid.getColumn("ID").setHidden(true);
        grid.getColumn("TITLE").setHeaderCaption("Title");
        grid.getColumn("DESCRIPTION").setHeaderCaption("Description");
        grid.getColumn("OPTLOCK").setHidden(true);
        grid.getColumn("USER_ID").setHidden(true);
        grid.getColumn("LAT").setHeaderCaption("Latitude");
        grid.getColumn("LONG").setHeaderCaption("Longitude");
        grid.getColumn("NAME").setHeaderCaption("Added by");


        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addSelectionListener(event -> {
            Item item = gridDs.getItem(event.getSelected().iterator().next());
            form.setDataForDispay(item);
        });

        leftSide.addComponent(grid);
        leftSide.setExpandRatio(grid, 1);

        // add buttons under the grid
        leftSide.addComponent(buttons);
        buttons.addComponent(edit);
        edit.addClickListener(event -> {
        });
        buttons.addComponent(newB);
        newB.addClickListener(event -> {
            form.setDataForDispay(gridDs.getItem(gridDs.addItem()));
            form.setFixedCoordinates(66, 22);
        });
        buttons.addComponent(delete);



        // setup adder / deleter
        form = new MyFormThing();
        horizontalLayout.addComponent(form);
    } // */

    /* works. Grid is 100% x 100% in the tabsheet
    public MyTableThing() {
        Grid grid;
        try {
            grid = new Grid(Freeform.getSqlContainer());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        grid.setSizeFull();
        setCompositionRoot(grid);
    } // */
}

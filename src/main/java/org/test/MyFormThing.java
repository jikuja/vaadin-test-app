package org.test;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.ui.*;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;


public class MyFormThing extends CustomComponent {
    @PropertyId("TITLE")
    private final TextField title = new TextField("Title");
    @PropertyId("DESCRIPTION")
    private final TextField description = new TextField("Description");
    @PropertyId("LAT")
    private final TextField lat = new TextField("Latitude");
    @PropertyId("LONG")
    private final TextField lon = new TextField("Longitude");
    @PropertyId("USER_ID")
    private final NativeSelect user = new NativeSelect("Added by");

    private final Button edit = new Button("Edit");
    private final Button save = new Button("Save");
    private final Button cancel = new Button("Cancel");

    private FieldGroup fieldGroup = new FieldGroup();

    private FormLayout layout = new FormLayout();

    public MyFormThing() {
        layout.addComponents(title, description, lat, lon, user);
        layout.addComponents(edit, save, cancel);

        disableFields();
        save.setVisible(false);
        cancel.setVisible(false);
        edit.setEnabled(true);
        setListeners();

        layout.setSizeUndefined();
        setCompositionRoot(layout);
    }

    // prefill form with known location
    public MyFormThing(double lat, double lon) {
        this();
        layout.setEnabled(true);
        edit.setVisible(false);
        save.setVisible(true);
        cancel.setVisible(true);
        this.lat.setValue(String.valueOf(lat));
        this.lat.setEnabled(false);
        this.lon.setValue(String.valueOf(lon));
        this.lon.setEnabled(false);
    }

    public void setDataForDispay(Item item) {
        if (fieldGroup.getItemDataSource() != item) {
            fieldGroup.discard(); // needed?
            fieldGroup.setItemDataSource(item);
            fieldGroup.bindMemberFields(this);
        }
    }

    public void setFixedCoordinates(double lat, double lon) {
        this.lat.setValue(String.valueOf(lat));
        this.lat.setEnabled(false);
        this.lon.setValue(String.valueOf(lon));
        this.lon.setEnabled(false);
    }

    protected  void setListeners() {
        edit.addClickListener(event -> {
            cancel.setVisible(true);
            save.setVisible(true);
            edit.setVisible(false);
        });

        // TODO: for map
        cancel.addClickListener(event -> {
            cancel.setVisible(false);
            save.setVisible(false);
            edit.setVisible(true);
        });

        save.addClickListener(event -> {
            try {
                fieldGroup.commit();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void enableFields(){
        title.setEnabled(true);
        description.setEnabled(true);
        lat.setEnabled(true);
        lon.setEnabled(true);
        user.setEnabled(true);
    }

    private void disableFields(){
        title.setEnabled(false);
        description.setEnabled(false);
        lat.setEnabled(false);
        lon.setEnabled(false);
        user.setEnabled(false);
    }
}

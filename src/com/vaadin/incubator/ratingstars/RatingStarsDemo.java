package com.vaadin.incubator.ratingstars;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.incubator.ratingstars.component.RatingStars;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

/**
 * A demo application for the RatingStars component. For a live demo see
 * {@link http://teemu.virtuallypreinstalled.com/RatingStars}.
 * 
 * @author Teemu Pöntelin / IT Mill Ltd
 */
public class RatingStarsDemo extends Application {

    private static final long serialVersionUID = 878415417860536617L;

    private Table table;

    private VerticalLayout mainLayout;

    private Set<RatingStars> allRatingStars = new HashSet<RatingStars>();

    private CheckBox animatedCheckBox;

    private String[] movieNames = { "The Matrix", "The Matrix Reloaded",
            "The Matrix Revolutions", "Memento", "Kill Bill: Vol. 1",
            "Kill Bill: Vol. 2", "District 9" };

    @Override
    public void init() {
        // set up the main window and main layout
        final Window mainWindow = new Window("RatingStarsDemoApplication");
        setMainWindow(mainWindow);
        mainLayout = new VerticalLayout();
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        mainWindow.setContent(mainLayout);

        // animated checkbox
        animatedCheckBox = new CheckBox("Animated?");
        animatedCheckBox.setValue(true);
        animatedCheckBox.setImmediate(true);
        animatedCheckBox.addListener(new Button.ClickListener() {

            private static final long serialVersionUID = -1291394320556343373L;

            public void buttonClick(ClickEvent event) {
                for (RatingStars rs : allRatingStars) {
                    rs.setAnimated((Boolean) event.getButton().getValue());
                }
            }
        });
        mainLayout.addComponent(animatedCheckBox);

        // create and populate the movie table
        table = new Table("Rate your favourite movies");
        table.addContainerProperty("Movie", String.class, null);
        table.addContainerProperty("Your rating", RatingStars.class, null);
        table.addContainerProperty("Average rating", RatingStars.class, null);
        populateTable();
        table.setPageLength(table.getItemIds().size());
        mainLayout.addComponent(table);

        // theme demos
        mainLayout.addComponent(new Label("<div style=\"margin-top: 20px\">"
                + "The component has two built-in styles.</div>",
                Label.CONTENT_XHTML));
        RatingStars defaultRs = new RatingStars();
        defaultRs.setCaption("default");
        allRatingStars.add(defaultRs);

        RatingStars largeRs = new RatingStars();
        largeRs.setStyleName("large");
        largeRs.setCaption("large");
        allRatingStars.add(largeRs);

        HorizontalLayout themeLayout = new HorizontalLayout();
        themeLayout.setSpacing(true);
        themeLayout.addComponent(defaultRs);
        themeLayout.addComponent(largeRs);
        mainLayout.addComponent(themeLayout);

        // component states
        mainLayout.addComponent(new Label("<div style=\"margin-top: 20px\">"
                + "Component states</div>", Label.CONTENT_XHTML));
        RatingStars disabledRs = new RatingStars();
        disabledRs.setCaption("disabled");
        disabledRs.setValue(2.5);
        disabledRs.setEnabled(false);

        RatingStars readonlyRs = new RatingStars();
        readonlyRs.setCaption("read-only");
        readonlyRs.setValue(2.5);
        readonlyRs.setReadOnly(true);

        HorizontalLayout stateLayout = new HorizontalLayout();
        stateLayout.setSpacing(true);
        stateLayout.addComponent(disabledRs);
        stateLayout.addComponent(readonlyRs);
        mainLayout.addComponent(stateLayout);
    }

    /**
     * Populate the table with some random data.
     */
    private void populateTable() {
        Random r = new Random();
        for (final String movieName : movieNames) {
            final RatingStars avgRs = new RatingStars();
            avgRs.setMaxValue(10);
            avgRs.setValue(r.nextFloat() * 9 + 1);
            avgRs.setReadOnly(true);
            allRatingStars.add(avgRs);

            final RatingStars yourRs = new RatingStars();
            yourRs.setMaxValue(10);
            yourRs.setImmediate(true);
            yourRs.addListener(new Property.ValueChangeListener() {

                private static final long serialVersionUID = -3277119031169194273L;

                public void valueChange(ValueChangeEvent event) {
                    Double value = (Double) event.getProperty().getValue();

                    RatingStarsDemo.this.getMainWindow().showNotification(
                            "You voted " + value + " stars for " + movieName
                                    + ".");

                    // dummy logic to calculate "average" value
                    avgRs.setReadOnly(false);
                    avgRs.setValue((((Double) avgRs.getValue()) + value) / 2);
                    avgRs.setReadOnly(true);
                }
            });
            allRatingStars.add(yourRs);

            Object itemId = table.addItem();
            Item i = table.getItem(itemId);
            i.getItemProperty("Movie").setValue(movieName);
            i.getItemProperty("Your rating").setValue(yourRs);
            i.getItemProperty("Average rating").setValue(avgRs);
        }
    }
}

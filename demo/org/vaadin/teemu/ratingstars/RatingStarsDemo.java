package org.vaadin.teemu.ratingstars;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * A demo application for the RatingStars component. For a live demo see
 * {@link http://teemu.virtuallypreinstalled.com/RatingStars}.
 * 
 * @author Teemu Pöntelin / Vaadin Ltd
 */
public class RatingStarsDemo extends UI {

    private static final long serialVersionUID = 7705972095201251401L;

    private Table table;
    private Set<RatingStars> allRatingStars = new HashSet<RatingStars>();
    private CheckBox animatedCheckBox;
    private VerticalLayout mainLayout;

    private String[] movieNames = { "The Matrix", "Memento",
            "Kill Bill: Vol. 1" };

    private static Map<Integer, String> valueCaptions = new HashMap<Integer, String>(
            5);

    static {
        valueCaptions.put(1, "Epic Fail");
        valueCaptions.put(2, "Poor");
        valueCaptions.put(3, "OK");
        valueCaptions.put(4, "Good");
        valueCaptions.put(5, "Excellent");
    }

    @Override
    protected void init(VaadinRequest request) {
        initWindowAndDescription();
        initDemoPanel();
    }

    private void initWindowAndDescription() {
        getPage().setTitle("RatingStars Component Demo");

        VerticalLayout centerLayout = new VerticalLayout();
        centerLayout.setMargin(true);

        mainLayout = new VerticalLayout();
        mainLayout.setMargin(true);
        Panel mainPanel = new Panel(mainLayout);
        mainPanel.setWidth("750px");
        centerLayout.addComponent(mainPanel);
        centerLayout.setComponentAlignment(mainPanel, Alignment.TOP_CENTER);
        setContent(centerLayout);

        StringBuilder descriptionXhtml = new StringBuilder();
        descriptionXhtml
                .append("<h1 style=\"margin: 0;\">RatingStars Component Demo</h1>");
        descriptionXhtml
                .append("<p>RatingStars is a simple component for giving rating values.</p>");
        descriptionXhtml
                .append("<p>Download and rate this component at <a href=\"http://vaadin.com/addon/ratingstars\">Vaadin Directory</a>. ");
        descriptionXhtml
                .append("Get the source code at <a href=\"https://github.com/tehapo/RatingStars\">GitHub</a>.</p>");
        descriptionXhtml.append("<p>Highlights:</p>");
        descriptionXhtml.append("<ul>");
        descriptionXhtml
                .append("<li>Keyboard usage (focus with tab, navigate with arrow keys, select with enter)</li>");
        descriptionXhtml.append("<li>Easily customizable appearance</li>");
        descriptionXhtml.append("<li>Captions for individual values</li>");
        descriptionXhtml.append("<li>Optional transition animations</li>");
        descriptionXhtml.append("</ul>");
        descriptionXhtml.append("<div style=\"height: 10px\"></div>");

        Label description = new Label(descriptionXhtml.toString(),
                ContentMode.HTML);
        mainLayout.addComponent(description);
    }

    private void initDemoPanel() {
        Panel demoPanel = new Panel("Demonstration");
        VerticalLayout demoLayout = new VerticalLayout();
        demoLayout.setSpacing(true);
        demoLayout.setMargin(true);
        demoPanel.setContent(demoLayout);
        mainLayout.addComponent(demoPanel);

        // animated checkbox
        animatedCheckBox = new CheckBox("Animated?");
        animatedCheckBox.setValue(true);
        animatedCheckBox.setImmediate(true);
        animatedCheckBox.addValueChangeListener(new ValueChangeListener() {

            private static final long serialVersionUID = 6001160591512323325L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                for (RatingStars rs : allRatingStars) {
                    rs.setAnimated((Boolean) event.getProperty().getValue());
                }
            }
        });
        demoLayout.addComponent(animatedCheckBox);

        // create and populate the movie table
        table = new Table("Rate your favourite movies");
        table.addContainerProperty("Movie", String.class, null);
        table.addContainerProperty("Comment", TextField.class, null);
        table.addContainerProperty("Your rating", RatingStars.class, null);
        table.addContainerProperty("Average rating", RatingStars.class, null);
        populateTable();
        table.setPageLength(table.getItemIds().size());
        demoLayout.addComponent(table);

        // theme demos
        demoLayout.addComponent(new Label(
                "<strong>The component has two built-in styles.</strong>",
                ContentMode.HTML));
        RatingStars defaultRs = new RatingStars();
        defaultRs.setCaption("default");
        allRatingStars.add(defaultRs);

        RatingStars tinyRs = new RatingStars();
        tinyRs.setStyleName("tiny");
        tinyRs.setCaption("tiny");
        allRatingStars.add(tinyRs);

        HorizontalLayout themeLayout = new HorizontalLayout();
        themeLayout.setSpacing(true);
        themeLayout.addComponent(defaultRs);
        themeLayout.addComponent(tinyRs);
        demoLayout.addComponent(themeLayout);

        // component states
        demoLayout.addComponent(new Label("<strong>Component states</strong>",
                ContentMode.HTML));
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
        demoLayout.addComponent(stateLayout);
    }

    /**
     * Populate the table with some random data.
     */
    private void populateTable() {
        Random r = new Random();
        for (final String movieName : movieNames) {
            final TextField textField = new TextField();

            final RatingStars avgRs = new RatingStars();
            avgRs.setMaxValue(5);
            avgRs.setValue(r.nextDouble() * 4 + 1);
            avgRs.setReadOnly(true);
            allRatingStars.add(avgRs);

            final RatingStars yourRs = new RatingStars();
            yourRs.setMaxValue(5);
            yourRs.setImmediate(true);
            yourRs.setDescription("Your rating");
            yourRs.setValueCaption(valueCaptions.values()
                    .toArray(new String[5]));
            yourRs.addValueChangeListener(new Property.ValueChangeListener() {

                private static final long serialVersionUID = 3978380217446180197L;

                public void valueChange(ValueChangeEvent event) {
                    Double value = (Double) event.getProperty().getValue();

                    Notification.show("You voted " + value + " stars for "
                            + movieName + ".",
                            Notification.Type.TRAY_NOTIFICATION);

                    RatingStars changedRs = (RatingStars) event.getProperty();
                    // reset value captions
                    changedRs.setValueCaption(valueCaptions.values().toArray(
                            new String[5]));
                    // set "Your Rating" caption
                    changedRs.setValueCaption((int) Math.round(value),
                            "Your Rating");

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
            i.getItemProperty("Comment").setValue(textField);
            i.getItemProperty("Your rating").setValue(yourRs);
            i.getItemProperty("Average rating").setValue(avgRs);
        }
    }

}

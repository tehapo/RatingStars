package org.vaadin.teemu.ratingstars.demo;

import javax.servlet.annotation.WebServlet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.teemu.ratingstars.RatingStars;

/**
 * A demo application for the RatingStars component. For a live demo see
 * <a href="http://teemu.virtuallypreinstalled.com/RatingStars">http://teemu.virtuallypreinstalled.com/RatingStars</a>
 *
 * @author Teemu Pöntelin
 */
@Theme("valo")
@Title("RatingStars Component Demo")
@SuppressWarnings("unused")
public class RatingStarsDemo extends UI {
    private static final long serialVersionUID = 7705972095201251401L;

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = RatingStarsDemo.class, productionMode = false,
            widgetset = "org.vaadin.teemu.ratingstars.demo.DemoWidgetSet")
    public static class MyUIServlet extends VaadinServlet {
    }

    private final static Map<Integer, String> valueCaptions = new HashMap<>(5, 1);

    static {
        valueCaptions.put(1, "Epic Fail");
        valueCaptions.put(2, "Poor");
        valueCaptions.put(3, "OK");
        valueCaptions.put(4, "Good");
        valueCaptions.put(5, "Excellent");
    }

    private final String[] movieNames = {"The Matrix", "Memento", "Kill Bill: Vol. 1"};

    private final Set<RatingStars> allRatingStars = new HashSet<>();

    private VerticalLayout mainLayout;

    @Override
    protected void init(VaadinRequest request) {
        initWindowAndDescription();
        initDemoPanel();
    }

    private void initWindowAndDescription() {
        VerticalLayout centerLayout = new VerticalLayout();

        mainLayout = new VerticalLayout();
        Panel mainPanel = new Panel(mainLayout);
        mainPanel.setWidth("750px");
        centerLayout.addComponent(mainPanel);
        centerLayout.setComponentAlignment(mainPanel, Alignment.TOP_CENTER);
        setContent(centerLayout);

        StringBuilder descriptionXhtml = new StringBuilder();
        descriptionXhtml.append("<h1 style=\"margin: 0;\">RatingStars Component Demo</h1>");
        descriptionXhtml.append("<p>RatingStars is a simple component for giving rating values.</p>");
        descriptionXhtml.append("<p>Download and rate this component at <a href=\"http://vaadin.com/addon/ratingstars\">Vaadin Directory</a>. ");
        descriptionXhtml.append("Get the source code at <a href=\"https://github.com/tehapo/RatingStars\">GitHub</a>.</p>");
        descriptionXhtml.append("<p>Highlights:</p>");
        descriptionXhtml.append("<ul>");
        descriptionXhtml.append("<li>Keyboard usage (focus with tab, navigate with arrow keys, select with enter)</li>");
        descriptionXhtml.append("<li>Easily customizable appearance</li>");
        descriptionXhtml.append("<li>Captions for individual values</li>");
        descriptionXhtml.append("<li>Optional transition animations</li>");
        descriptionXhtml.append("</ul>");
        descriptionXhtml.append("<div style=\"height: 10px\"></div>");

        Label description = new Label(descriptionXhtml.toString(), ContentMode.HTML);
        mainLayout.addComponent(description);
    }

    private void initDemoPanel() {
        Panel demoPanel = new Panel("Demonstration");
        VerticalLayout demoLayout = new VerticalLayout();
        demoPanel.setContent(demoLayout);
        mainLayout.addComponent(demoPanel);

        // animated checkbox
        CheckBox animatedCheckBox = new CheckBox("Animated?");
        animatedCheckBox.setValue(true);
        animatedCheckBox.addValueChangeListener(event -> {
            for (RatingStars rs : allRatingStars) {
                rs.setAnimated(event.getValue());
            }
        });

        demoLayout.addComponents(animatedCheckBox, createMovieDemo(), createThemeDemos());
    }

    private VerticalLayout createThemeDemos() {
        VerticalLayout themeDemos = new VerticalLayout();
        themeDemos.setMargin(false);

        // theme demos
        themeDemos.addComponent(new Label("<strong>The component has two built-in styles.</strong>", ContentMode.HTML));

        RatingStars defaultRs = new RatingStars();
        defaultRs.setDescription("Default RatingStars");
        defaultRs.setCaption("default");
        allRatingStars.add(defaultRs);

        RatingStars tinyRs = new RatingStars();
        tinyRs.setMaxValue(3);
        tinyRs.setStyleName("tiny");
        tinyRs.setCaption("tiny");
        allRatingStars.add(tinyRs);

        themeDemos.addComponent(new HorizontalLayout(defaultRs, tinyRs));

        // component states
        themeDemos.addComponent(new Label("<strong>Component states</strong>", ContentMode.HTML));

        RatingStars disabledRs = new RatingStars();
        disabledRs.setCaption("disabled");
        disabledRs.setValue(2.5);
        disabledRs.setEnabled(false);

        RatingStars readonlyRs = new RatingStars();
        readonlyRs.setCaption("read-only");
        readonlyRs.setValue(2.5);
        readonlyRs.setReadOnly(true);

        themeDemos.addComponent(new HorizontalLayout(disabledRs, readonlyRs));

        return themeDemos;
    }

    private VerticalLayout createMovieDemo() {
        VerticalLayout movieDemo = new VerticalLayout();
        movieDemo.setMargin(false);
        movieDemo.addComponent(new Label("Rate your favourite movies:"));

        for (final String movieName : movieNames) {
            final RatingStars averageRating = new RatingStars();
            averageRating.setMaxValue(5);
            averageRating.setValue(ThreadLocalRandom.current().nextDouble(1, 5));
            averageRating.setReadOnly(true);
            allRatingStars.add(averageRating);

            final RatingStars userRating = new RatingStars();
            userRating.setMaxValue(5);
            userRating.setValueCaption(valueCaptions.values().toArray(new String[5]));
            userRating.addValueChangeListener(event -> {
                Double value = event.getValue();

                Notification.show("You voted " + value + " stars for " + movieName + ".", Notification.Type.TRAY_NOTIFICATION);

                RatingStars changedRs = (RatingStars) event.getComponent();
                // reset value captions
                changedRs.setValueCaption(valueCaptions.values().toArray(new String[5]));
                // set "Your Rating" caption
                changedRs.setValueCaption((int) Math.round(value), "Your Rating");

                // dummy logic to calculate "average" value
                averageRating.setValue((averageRating.getValue() + value) / 2);
            });

            allRatingStars.add(userRating);

            Label movieNameLabel = new Label(movieName);
            movieNameLabel.setWidth("100px");
            HorizontalLayout movieRow = new HorizontalLayout(movieNameLabel, userRating, averageRating);
            movieRow.setMargin(false);
            movieDemo.addComponent(movieRow);
        }

        return movieDemo;
    }

}

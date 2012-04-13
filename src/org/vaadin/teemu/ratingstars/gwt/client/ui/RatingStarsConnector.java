package org.vaadin.teemu.ratingstars.gwt.client.ui;

import org.vaadin.teemu.ratingstars.RatingStars;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.AbstractFieldConnector;
import com.vaadin.terminal.gwt.client.ui.Connect;

// Deprecated stuff will be removed when doing proper conversion to Vaadin 7.
@SuppressWarnings("deprecation")
@Connect(RatingStars.class)
public class RatingStarsConnector extends AbstractFieldConnector implements
        Paintable {

    private static final long serialVersionUID = -366120906521723420L;

    @Override
    protected Widget createWidget() {
        return GWT.create(VRatingStars.class);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        ((Paintable) getWidget()).updateFromUIDL(uidl, client);
    }

}

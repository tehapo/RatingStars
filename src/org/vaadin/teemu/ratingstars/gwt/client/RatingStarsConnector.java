package org.vaadin.teemu.ratingstars.gwt.client;

import org.vaadin.teemu.ratingstars.RatingStars;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.shared.ui.Connect;

@Connect(RatingStars.class)
public class RatingStarsConnector extends AbstractFieldConnector implements
        Paintable {

    private static final long serialVersionUID = -366120906521723420L;

    @Override
    protected Widget createWidget() {
        return GWT.create(RatingStarsWidget.class);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        ((Paintable) getWidget()).updateFromUIDL(uidl, client);
    }

}

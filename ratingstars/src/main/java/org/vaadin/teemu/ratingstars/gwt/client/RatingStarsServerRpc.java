package org.vaadin.teemu.ratingstars.gwt.client;

import com.vaadin.shared.communication.ServerRpc;

public interface RatingStarsServerRpc extends ServerRpc {

    /**
     * Called by the {@link RatingStarsConnector} when the value of
     * {@link RatingStarsWidget} is changed.
     * 
     * @param newValue
     */
    void valueChanged(double newValue);

}

package org.vaadin.teemu.ratingstars.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.shared.ui.Connect;
import org.vaadin.teemu.ratingstars.RatingStars;

@Connect(RatingStars.class)
public class RatingStarsConnector extends AbstractFieldConnector implements
        ValueChangeHandler<Double> {

    private static final long serialVersionUID = -5575779693149883388L;

    private RatingStarsServerRpc rpc = RpcProxy.create(
            RatingStarsServerRpc.class, this);

    @Override
    protected void init() {
        super.init();
        getWidget().addValueChangeHandler(this);
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(RatingStarsWidget.class);
    }

    @Override
    public RatingStarsWidget getWidget() {
        return (RatingStarsWidget) super.getWidget();
    }

    @Override
    public RatingStarsState getState() {
        return (RatingStarsState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        getWidget().setReadOnly(getState().readOnly);
        getWidget().updateValueCaptions(getState().valueCaptions);
    }

    @Override
    public void onValueChange(ValueChangeEvent<Double> event) {
        rpc.valueChanged(event.getValue());
    }
}

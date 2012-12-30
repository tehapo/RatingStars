package org.vaadin.teemu.ratingstars;

import org.vaadin.teemu.ratingstars.gwt.client.RatingStarsServerRpc;
import org.vaadin.teemu.ratingstars.gwt.client.RatingStarsState;

import com.vaadin.ui.AbstractField;

/**
 * RatingStars is a typical rating component seen in many web applications.
 * 
 * @author Teemu Pöntelin / Vaadin Ltd
 */
public class RatingStars extends AbstractField<Double> implements
        Comparable<RatingStars> {

    private static final long serialVersionUID = 4689425856123104186L;

    private final RatingStarsServerRpc rpc = new RatingStarsServerRpc() {

        private static final long serialVersionUID = -7317353863605973697L;

        @Override
        public void valueChanged(double newValue) {
            setValue(newValue);
        }
    };

    /**
     * Constructs a new animated RatingStars component with default value of 0.0
     * and default maximum value of five.
     * 
     * @see #setMaxValue(int)
     * @see #setAnimated(boolean)
     */
    public RatingStars() {
        registerRpc(rpc);

        // Set the defaults.
        setValue(0.0);
        setMaxValue(5);
        setAnimated(true);
    }

    @Override
    protected RatingStarsState getState() {
        return (RatingStarsState) super.getState();
    }

    /**
     * Sets the maximum value (rating) the user can give. The default value is
     * five.
     * 
     * @param maxValue
     */
    public void setMaxValue(int maxValue) {
        if (maxValue <= 0) {
            throw new IllegalArgumentException("Given maximum value ("
                    + maxValue + ") must be greater than zero.");
        }
        getState().maxValue = maxValue;
    }

    /**
     * Sets whether the client-side transitions should be animated or not. The
     * default value is <code>true</code>.
     * 
     * @param animated
     *            should the client-side transitions be animated
     */
    public void setAnimated(boolean animated) {
        getState().animated = animated;
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }

    @Override
    public void setValue(Double value) {
        super.setValue(value);
        getState().value = value;
    }

    @Override
    public int compareTo(RatingStars o) {
        return ((Double) this.getValue()).compareTo((Double) o.getValue());
    }

    /**
     * Sets the caption as the displayed description of the given rating value.
     * 
     * @param value
     * @param captionForValue
     * @see #setValueCaption(String...)
     */
    public void setValueCaption(int value, String captionForValue) {
        getState().valueCaptions.put(value, captionForValue);
    }

    /**
     * Sets all given captions as displayed descriptions for rating values in
     * sequence starting from 1.
     * 
     * @param captionsForValues
     * @see #setValueCaption(int, String)
     */
    public void setValueCaption(String... captionsForValues) {
        int value = 1;
        for (String caption : captionsForValues) {
            getState().valueCaptions.put(value++, caption);
        }
    }

}

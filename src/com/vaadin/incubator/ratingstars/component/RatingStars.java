package com.vaadin.incubator.ratingstars.component;

import java.util.Map;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractField;

/**
 * RatingStars is a typical rating component seen in many web applications.
 * 
 * @author Teemu Pöntelin / IT Mill Ltd
 */
@SuppressWarnings("unchecked")
public class RatingStars extends AbstractField {

    private static final long serialVersionUID = -3594458506374404590L;

    private int maxValue = 5;

    private boolean animated = true;

    /**
     * Constructs a new animated RatingStars component with default value of 0.0
     * and default maximum value of five.
     * 
     * @see #setMaxValue(int)
     * @see #setAnimated(boolean)
     */
    public RatingStars() {
        setValue(0.0); // default to zero
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
        this.maxValue = maxValue;
    }

    /**
     * Sets whether the client-side transitions should be animated or not. The
     * default value is <code>true</code>.
     * 
     * @param animated
     *            should the client-side transitions be animated
     */
    public void setAnimated(boolean animated) {
        if (this.animated != animated) {
            this.animated = animated;
            requestRepaint();
        }
    }

    @Override
    public Class getType() {
        return Double.class;
    }

    @Override
    public String getTag() {
        return "ratingstars";
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        target.addAttribute("maxValue", maxValue);
        target.addAttribute("value", Double.valueOf(getValue().toString()));
        target.addAttribute("animated", animated);
    }

    @Override
    public void setValue(Object value) {
        super.setValue(Double.valueOf(value.toString()));
    }

    @Override
    public void changeVariables(Object source, Map variables) {
        super.changeVariables(source, variables);

        if (variables.containsKey("value")) {
            setValue(Double.valueOf(variables.get("value").toString()));
        }
    }

}

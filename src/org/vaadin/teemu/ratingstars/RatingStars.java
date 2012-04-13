package org.vaadin.teemu.ratingstars;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Vaadin6Component;
import com.vaadin.ui.AbstractField;

/**
 * RatingStars is a typical rating component seen in many web applications.
 * 
 * @author Teemu Pöntelin / Vaadin Ltd
 */
public class RatingStars extends AbstractField<Double> implements
        Comparable<RatingStars>, Vaadin6Component {

    private static final long serialVersionUID = 5891927840599037355L;

    public static final String ATTR_MAX_VALUE = "maxValue";
    public static final String ATTR_VALUE = "value";
    public static final String ATTR_VALUE_CAPTIONS = "valueCaptions";
    public static final String ATTR_ANIMATED = "animated";
    public static final String ATTR_IMMEDIATE = "immediate";
    public static final String ATTR_READONLY = "readonly";
    public static final String ATTR_DISABLED = "disabled";

    private int maxValue = 5;

    private boolean animated = true;

    private Map<Integer, String> valueCaptions = new HashMap<Integer, String>();

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
    public Class<Double> getType() {
        return Double.class;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        target.addAttribute(ATTR_IMMEDIATE, isImmediate());
        target.addAttribute(ATTR_DISABLED, !isEnabled());
        target.addAttribute(ATTR_READONLY, isReadOnly());
        target.addAttribute(ATTR_MAX_VALUE, maxValue);
        target.addAttribute(ATTR_ANIMATED, animated);
        target.addAttribute(ATTR_VALUE_CAPTIONS, valueCaptions);
        target.addVariable(this, ATTR_VALUE,
                Double.valueOf(getValue().toString()));
    }

    @Override
    public void setValue(Object value) {
        super.setValue(Double.valueOf(value.toString()));
    }

    @SuppressWarnings("rawtypes")
    public void changeVariables(Object source, Map variables) {
        if (variables.containsKey("value")) {
            setValue(Double.valueOf(variables.get("value").toString()));
        }
    }

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
        valueCaptions.put(value, captionForValue);
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
            valueCaptions.put(value++, caption);
        }
    }

}

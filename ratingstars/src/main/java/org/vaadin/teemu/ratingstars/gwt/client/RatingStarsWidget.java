package org.vaadin.teemu.ratingstars.gwt.client;

import java.util.Map;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.HasValue;

/**
 * RatingStarsWidget is the client-side implementation of the RatingStars
 * component.
 * 
 * The DOM tree for this component is constructed as follows:
 * 
 * <pre>
 *    div.v-ratingstars-wrapper
 *        div.v-ratingstars
 *            div.v-ratingstars-star
 *            div.v-ratingstars-star
 *            div.v-ratingstars-star
 *            ...
 *            div.v-ratingstars-star
 *            div.v-ratingstars-bar
 * </pre>
 * 
 * The idea behind the DOM tree is that {@code .v-ratingstars-star} elements
 * always have a partially transparent background image and the width of the
 * {@code .v-ratingstars-bar} element behind these star elements is changed
 * according to the current value.
 * 
 * @author Teemu Pöntelin / Vaadin Ltd
 */
public class RatingStarsWidget extends FocusWidget implements HasAnimation,
        HasValue<Double>, HasValueChangeHandlers<Double> {

    /** Set the CSS class names to allow styling. */
    public static final String CLASSNAME = "v-ratingstars";
    public static final String STAR_CLASSNAME = CLASSNAME + "-star";
    public static final String BAR_CLASSNAME = CLASSNAME + "-bar";
    public static final String WRAPPER_CLASSNAME = CLASSNAME + "-wrapper";

    private static final int ANIMATION_DURATION_IN_MS = 150;

    // DOM elements
    private Element barDiv;
    private Element element;
    private Element[] starElements;

    /** Currently focused star (by keyboard focus). */
    private int focusIndex = -1;

    private int maxValue = 5;
    private double value;

    private boolean animated;
    private boolean readonly;

    public RatingStarsWidget() {
        setElement(Document.get().createDivElement());
        setStyleName(WRAPPER_CLASSNAME);
        initDom();
    }

    private void initDom() {
        if (element != null) {
            // Remove previous element.
            getElement().removeChild(element);
        }

        element = Document.get().createDivElement();
        element.setClassName(CLASSNAME);
        getElement().appendChild(element);

        createStarElements();

        barDiv = createBarDiv();
        element.appendChild(barDiv);

        DOM.sinkEvents(getElement(), Event.ONCLICK | Event.ONMOUSEOVER
                | Event.ONMOUSEOUT | Event.ONFOCUS | Event.ONBLUR
                | Event.ONKEYUP);
    }

    void updateValueCaptions(Map<Integer, String> valueCaptions) {
        for (Element starElement : starElements) {
            int rating = starElement.getPropertyInt("rating");
            String caption = valueCaptions.get(rating);
            if (caption != null) {
                starElement.setPropertyString("caption", caption);

                if (StarCaptionUtil.isVisibleForStarElement(starElement)) {
                    // update currently visible caption
                    StarCaptionUtil.showAroundElement(starElement, caption);
                }
            }
        }
    }

    private void createStarElements() {
        starElements = new Element[maxValue];
        for (int i = 0; i < maxValue; i++) {
            DivElement starDiv = createStarDiv(i + 1);
            starElements[i] = starDiv;
            element.appendChild(starDiv);
        }
    }

    @Override
    public void onBrowserEvent(Event event) {
        if (!isEnabled() || readonly) {
            return; // Do nothing if disabled or read-only.
        }

        super.onBrowserEvent(event);

        Element target = Element.as(event.getEventTarget());
        switch (DOM.eventGetType(event)) {
        case Event.ONCLICK:
            // update value
            setValueFromElement(target);
            break;
        case Event.ONMOUSEOVER:
            // animate
            if (target.getClassName().contains(STAR_CLASSNAME)) {
                int rating = target.getPropertyInt("rating");
                setFocusIndex(rating - 1);
                setFocus(true);
                StarCaptionUtil.showAroundElement(target,
                        target.getPropertyString("caption"));
            }
            break;
        case Event.ONMOUSEOUT:
            setBarWidth(calcBarWidth(value));
            setFocusIndex(-1);
            StarCaptionUtil.hide();
            break;
        case Event.ONFOCUS:
            getElement().addClassName(WRAPPER_CLASSNAME + "-focus");
            if (focusIndex < 0) {
                if (Math.round(value) > 0) {
                    // focus the current value (or the closest int)
                    setFocusIndex((int) (Math.round(value) - 1));
                } else {
                    // focus the first value
                    setFocusIndex(0);
                }
            }
            break;
        case Event.ONBLUR:
            getElement().removeClassName(WRAPPER_CLASSNAME + "-focus");
            setFocusIndex(-1);
            setBarWidth(calcBarWidth(value));
            StarCaptionUtil.hide();
            break;
        case Event.ONKEYUP:
            handleKeyUp(event);
            break;
        }
    }

    private void setFocusIndex(int index) {
        // remove old focus class
        if (focusIndex >= 0 && focusIndex < starElements.length) {
            starElements[focusIndex].removeClassName(STAR_CLASSNAME + "-focus");
        }
        // update focusIndex and add class
        focusIndex = index;
        if (focusIndex >= 0 && focusIndex < starElements.length) {
            Element focusedStar = starElements[focusIndex];

            focusedStar.addClassName(STAR_CLASSNAME + "-focus");
            setBarWidth(calcBarWidth(focusedStar.getPropertyInt("rating")));
            StarCaptionUtil.showAroundElement(focusedStar,
                    focusedStar.getPropertyString("caption"));
        }
    }

    private void changeFocusIndex(int delta) {
        int newFocusIndex = focusIndex + delta;

        // check for boundaries
        if (newFocusIndex >= 0 && newFocusIndex < starElements.length) {
            setFocusIndex(newFocusIndex);
        }
    }

    private void setValueFromElement(Element target) {
        if (target.getClassName().contains(STAR_CLASSNAME)) {
            int ratingValue = target.getPropertyInt("rating");
            setValue((double) ratingValue, true);
        }
    }

    public void handleKeyUp(Event event) {
        if (event.getKeyCode() == KeyCodes.KEY_RIGHT) {
            changeFocusIndex(+1);
        } else if (event.getKeyCode() == KeyCodes.KEY_LEFT) {
            changeFocusIndex(-1);
        } else if (event.getKeyCode() == KeyCodes.KEY_ENTER) {
            setValueFromElement(starElements[focusIndex]);
        }
    }

    /**
     * Creates the DivElement of the bar representing the current value.
     * 
     * @return the newly created DivElement representing the bar.
     * @see #setBarWidth(byte)
     */
    private Element createBarDiv() {
        DivElement barDiv = Document.get().createDivElement();
        barDiv.setClassName(BAR_CLASSNAME);
        barDiv.getStyle().setProperty("width", calcBarWidth(value) + "%");
        return barDiv;
    }

    /**
     * Sets the width of the bar div instantly or via animated progress
     * depending on the value of the <code>animated</code> property.
     */
    private void setBarWidth(final byte widthPercentage) {
        if (barDiv == null) {
            return;
        }

        final byte currentWidthPercentage = getCurrentBarWidth();
        if (currentWidthPercentage != widthPercentage) {
            if (!isAnimationEnabled()) {
                barDiv.getStyle().setProperty("width", widthPercentage + "%");
            } else {
                Animation animation = new Animation() {
                    @Override
                    protected void onUpdate(double progress) {
                        byte newWidth = (byte) (currentWidthPercentage + (progress * (widthPercentage - currentWidthPercentage)));
                        barDiv.getStyle().setProperty("width", newWidth + "%");
                    }
                };
                animation.run(ANIMATION_DURATION_IN_MS);
            }
        }
    }

    private byte getCurrentBarWidth() {
        String currentWidth = barDiv.getStyle().getProperty("width");
        byte currentWidthPercentage = 0;
        if (currentWidth != null && currentWidth.length() > 0) {
            currentWidthPercentage = Byte.valueOf(currentWidth.substring(0,
                    currentWidth.length() - 1));
        }
        return currentWidthPercentage;
    }

    /**
     * Calculates the bar width for the given <code>forValue</code> as a
     * percentage of the <code>maxValue</code>. Returned value is from 0 to 100.
     * 
     * @return width percentage (0..100)
     */
    private byte calcBarWidth(double forValue) {
        return (byte) (forValue * 100 / maxValue);
    }

    /**
     * Creates a DivElement representing a single star. Given
     * <code>rating</code> value is set as an int property for the div.
     * 
     * @param rating
     *            rating value of this star.
     * @return a DivElement representing a single star.
     */
    private DivElement createStarDiv(int rating) {
        DivElement starDiv = Document.get().createDivElement();
        starDiv.setClassName(STAR_CLASSNAME);
        starDiv.setPropertyInt("rating", rating);
        return starDiv;
    }

    @Override
    public boolean isAnimationEnabled() {
        return animated;
    }

    @Override
    public void setAnimationEnabled(boolean enable) {
        this.animated = enable;
    }

    public void setMaxValue(int maxValue) {
        if (this.maxValue != maxValue) {
            this.maxValue = maxValue;
            initDom(); // Recreate the DOM.
        }
    }

    private void internalSetValue(double value) {
        if (this.value != value) {
            this.value = value;
            setBarWidth(calcBarWidth(this.value));
        }
    }

    public void setReadOnly(boolean readonly) {
        if (this.readonly != readonly) {
            this.readonly = readonly;
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(
            ValueChangeHandler<Double> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public void setValue(Double value) {
        setValue(value, false);
    }

    @Override
    public void setValue(Double value, boolean fireEvents) {
        // Null not supported -> convert to zero.
        if (value == null) {
            value = 0.0;
        }

        ValueChangeEvent.fireIfNotEqual(this, this.value, value);
        internalSetValue(value);
    }
}

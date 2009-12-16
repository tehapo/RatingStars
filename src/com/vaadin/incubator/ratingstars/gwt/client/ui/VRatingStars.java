package com.vaadin.incubator.ratingstars.gwt.client.ui;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasAnimation;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public class VRatingStars extends FocusWidget implements Paintable,
        HasAnimation {

    /** Set the tagname used to statically resolve widget from UIDL. */
    public static final String TAGNAME = "ratingstars";

    /** Set the CSS class names to allow styling. */
    public static final String CLASSNAME = "v-" + TAGNAME;
    public static final String STAR_CLASSNAME = CLASSNAME + "-star";
    public static final String BAR_CLASSNAME = CLASSNAME + "-bar";
    public static final String WRAPPER_CLASSNAME = CLASSNAME + "-wrapper";

    private static final int ANIMATION_DURATION_IN_MS = 150;

    /** Component identifier in UIDL communications. */
    String uidlId;

    /** Reference to the server connection object. */
    ApplicationConnection client;

    private Element barDiv;

    private Element element;

    private int width;

    private int height;

    private boolean hasFocus;

    private Element[] starElements;

    private int kbFocusIndex;

    private boolean mouseDown;

    /** Values from the UIDL */
    private static final String ATTR_MAX_VALUE = "maxValue";
    private static final String ATTR_VALUE = "value";
    private static final String ATTR_ANIMATED = "animated";
    private static final String ATTR_IMMEDIATE = "immediate";
    private static final String ATTR_READONLY = "readonly";
    private static final String ATTR_DISABLED = "disabled";
    private int maxValue;
    private double value;
    private boolean animated;
    private boolean immediate;
    private boolean readonly;
    private boolean disabled;

    public VRatingStars() {
        setElement(Document.get().createDivElement());
        setStyleName(WRAPPER_CLASSNAME);

        element = Document.get().createDivElement();
        element.setClassName(CLASSNAME);
        getElement().appendChild(element);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // This call should be made first. Ensure correct implementation,
        // and let the containing layout manage caption, etc.
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        // Save reference to server connection object to be able to send
        // user interaction later
        this.client = client;

        // Save the UIDL identifier for the component
        uidlId = uidl.getId();

        // Collect the relevant values from UIDL
        maxValue = uidl.getIntAttribute(ATTR_MAX_VALUE);
        immediate = uidl.getBooleanAttribute(ATTR_IMMEDIATE);
        disabled = uidl.getBooleanAttribute(ATTR_DISABLED);
        readonly = uidl.getBooleanAttribute(ATTR_READONLY);
        setAnimationEnabled(uidl.getBooleanAttribute(ATTR_ANIMATED));
        value = uidl.getDoubleVariable(ATTR_VALUE);

        if (!disabled && !readonly) {
            sinkEvents(Event.ONMOUSEUP);
            sinkEvents(Event.ONMOUSEDOWN);
            sinkEvents(Event.ONMOUSEOVER);
            sinkEvents(Event.ONMOUSEOUT);
            sinkEvents(Event.ONFOCUS);
            sinkEvents(Event.ONBLUR);
            sinkEvents(Event.ONKEYUP);
        } else {
            DOM.sinkEvents(getElement(), 0);
            setTabIndex(-1);
        }

        if (barDiv == null) {
            // DOM structure not yet constructed
            starElements = new Element[maxValue];
            for (int i = 0; i < maxValue; i++) {
                DivElement starDiv = createStarDiv(i + 1);
                starElements[i] = starDiv;
                element.appendChild(starDiv);
                width += starDiv.getClientWidth();
                if (height < starDiv.getClientHeight()) {
                    height = starDiv.getClientHeight();
                }
            }
            barDiv = createBarDiv(height);
            element.appendChild(barDiv);
        } else {
            setBarWidth(calcBarWidth(value));
        }
        element.getStyle().setPropertyPx("width", width);
        element.getStyle().setPropertyPx("height", height);
    }

    @Override
    public void onBrowserEvent(Event event) {
        if (uidlId == null || client == null) {
            return;
        }

        super.onBrowserEvent(event);

        Element target = Element.as(event.getEventTarget());
        switch (DOM.eventGetType(event)) {
        case Event.ONMOUSEUP:
            // update value
            setValue(target);
            setFocus(false);
            mouseDown = false;
            break;
        case Event.ONMOUSEDOWN:
            mouseDown = true;
            break;
        case Event.ONMOUSEOVER:
            // animate
            if (target.getClassName().contains(STAR_CLASSNAME)) {
                setBarWidth(calcBarWidth(target.getPropertyInt("rating")));
            }
            break;
        case Event.ONMOUSEOUT:
            // animate
            setBarWidth(calcBarWidth(value));
            break;
        case Event.ONFOCUS:
            hasFocus = true;
            if (!mouseDown) {
                if (Math.round(value) > 0) {
                    // focus the current value (or the closest int)
                    kbFocusIndex = (int) (Math.round(value) - 1);
                } else {
                    // focus the first
                    kbFocusIndex = 0;
                }
                updateKeyboardFocus();
            }
            break;
        case Event.ONBLUR:
            hasFocus = false;
            removeKeyboardFocus();
            break;
        case Event.ONKEYUP:
            handleKeyUp(event);
            break;
        }
    }

    private void setValue(Element target) {
        if (target.getClassName().contains(STAR_CLASSNAME)) {
            int ratingValue = target.getPropertyInt("rating");
            value = ratingValue;
            client.updateVariable(uidlId, "value", ratingValue, immediate);
        }
    }

    private void removeKeyboardFocus() {
        String className = starElements[kbFocusIndex].getClassName();
        className = className.replaceAll(STAR_CLASSNAME + "-focused", "")
                .trim();
        starElements[kbFocusIndex].setClassName(className);
        if (!hasFocus) {
            // revert to the current value
            setBarWidth(calcBarWidth(value));
        }
    }

    private void updateKeyboardFocus() {
        String className = starElements[kbFocusIndex].getClassName();
        if (!className.contains(STAR_CLASSNAME + "-focused")) {
            starElements[kbFocusIndex].setClassName(className + " "
                    + STAR_CLASSNAME + "-focused");
        }
        setBarWidth(calcBarWidth(starElements[kbFocusIndex]
                .getPropertyInt("rating")));
    }

    public void handleKeyUp(Event event) {
        if (hasFocus) {
            int focusChange = 0;
            if (event.getKeyCode() == KeyCodes.KEY_RIGHT) {
                if (kbFocusIndex < starElements.length - 1) {
                    focusChange = 1;
                }
            } else if (event.getKeyCode() == KeyCodes.KEY_LEFT) {
                if (kbFocusIndex > 0) {
                    focusChange = -1;
                }
            } else if (event.getKeyCode() == KeyCodes.KEY_ENTER) {
                setValue(starElements[kbFocusIndex]);
            }

            // update keyboard focus value
            if (focusChange != 0) {
                removeKeyboardFocus();
                kbFocusIndex += focusChange;
                updateKeyboardFocus();
            }
        }
    }

    /**
     * Creates the DivElement of the bar representing the current value.
     * 
     * @return the newly created DivElement representing the bar.
     * @see #setBarWidth(byte)
     */
    private DivElement createBarDiv(int height) {
        DivElement barDiv = Document.get().createDivElement();
        barDiv.setClassName(BAR_CLASSNAME);
        barDiv.getStyle().setProperty("width", calcBarWidth(value) + "%");
        barDiv.getStyle().setPropertyPx("height", height);
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

        if (!isAnimationEnabled()) {
            barDiv.getStyle().setProperty("width", widthPercentage + "%");
        } else {
            String currentWidth = barDiv.getStyle().getProperty("width");
            final byte startPercentage = Byte.valueOf(currentWidth.substring(0,
                    currentWidth.length() - 1));
            Animation animation = new Animation() {
                @Override
                protected void onUpdate(double progress) {
                    byte newWidth = (byte) (startPercentage + (progress * (widthPercentage - startPercentage)));
                    barDiv.getStyle().setProperty("width", newWidth + "%");
                }
            };
            animation.run(ANIMATION_DURATION_IN_MS);
        }
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

}

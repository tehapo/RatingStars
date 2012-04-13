package org.vaadin.teemu.ratingstars.gwt.client.ui;

import org.vaadin.teemu.ratingstars.RatingStars;

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
import com.vaadin.terminal.gwt.client.ValueMap;

/**
 * VRatingStars is the client-side implementation of the RatingStars component.
 * 
 * The DOM structure is as follows:
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
 * The main idea is that .v-ratingstars-star elements have a partially
 * transparent background image and the width of the .v-ratingstars-bar element
 * behind these star elements is changed according to the current value.
 * 
 * @author Teemu Pöntelin / Vaadin Ltd
 */
// Deprecated stuff will be removed when doing proper conversion to Vaadin 7.
@SuppressWarnings("deprecation")
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

    private Element[] starElements;

    private int focusIndex = -1;

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
        maxValue = uidl.getIntAttribute(RatingStars.ATTR_MAX_VALUE);
        immediate = uidl.getBooleanAttribute(RatingStars.ATTR_IMMEDIATE);
        disabled = uidl.getBooleanAttribute(RatingStars.ATTR_DISABLED);
        readonly = uidl.getBooleanAttribute(RatingStars.ATTR_READONLY);
        setAnimationEnabled(uidl.getBooleanAttribute(RatingStars.ATTR_ANIMATED));
        value = uidl.getDoubleVariable(RatingStars.ATTR_VALUE);

        if (!disabled && !readonly) {
            sinkEvents(Event.ONCLICK);
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
            createStarElements();
            initWidthAndHeight();
            element.getStyle().setPropertyPx("width", width);
            element.getStyle().setPropertyPx("height", height);

            barDiv = createBarDiv(height);
            element.appendChild(barDiv);
        } else {
            setBarWidth(calcBarWidth(value));
        }
        updateValueCaptions(uidl
                .getMapAttribute(RatingStars.ATTR_VALUE_CAPTIONS));
    }

    private void updateValueCaptions(ValueMap valueCaptions) {
        for (Element starElement : starElements) {
            int rating = starElement.getPropertyInt("rating");
            String caption = valueCaptions.getString(Integer.toString(rating));
            if (caption != null) {
                starElement.setPropertyString("caption", caption);

                String captionId = getCaptionId(rating);
                if (VStarCaption.isVisibleForCaptionId(captionId)) {
                    // update currently visible caption
                    VStarCaption.showAroundElement(starElement, caption,
                            captionId);
                }
            }
        }
    }

    private String getCaptionId(int rating) {
        return uidlId + "-" + rating;
    }

    private void createStarElements() {
        starElements = new Element[maxValue];
        for (int i = 0; i < maxValue; i++) {
            DivElement starDiv = createStarDiv(i + 1);
            starElements[i] = starDiv;
            element.appendChild(starDiv);
        }
    }

    /**
     * Star elements must be created before calling this method.
     */
    private void initWidthAndHeight() {
        for (Element starDiv : starElements) {
            width += starDiv.getClientWidth();
            if (height < starDiv.getClientHeight()) {
                height = starDiv.getClientHeight();
            }
        }
    }

    @Override
    public void onBrowserEvent(Event event) {
        if (uidlId == null || client == null) {
            return;
        }

        super.onBrowserEvent(event);

        Element target = Element.as(event.getEventTarget());
        switch (DOM.eventGetType(event)) {
        case Event.ONCLICK:
            // update value
            setValue(target);
            break;
        case Event.ONMOUSEOVER:
            // animate
            if (target.getClassName().contains(STAR_CLASSNAME)) {
                int rating = target.getPropertyInt("rating");
                setFocusIndex(rating - 1);
                setFocus(true);
                VStarCaption.showAroundElement(target,
                        target.getPropertyString("caption"),
                        getCaptionId(target.getPropertyInt("rating")));
            }
            break;
        case Event.ONMOUSEOUT:
            setBarWidth(calcBarWidth(value));
            setFocusIndex(-1);
            VStarCaption.hide();
            break;
        case Event.ONFOCUS:
            addClassName(getElement(), WRAPPER_CLASSNAME + "-focus");
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
            removeClassName(getElement(), WRAPPER_CLASSNAME + "-focus");
            setFocusIndex(-1);
            setBarWidth(calcBarWidth(value));
            VStarCaption.hide();
            break;
        case Event.ONKEYUP:
            handleKeyUp(event);
            break;
        }
    }

    private void setFocusIndex(int index) {
        // remove old focus class
        if (focusIndex >= 0 && focusIndex < starElements.length) {
            removeClassName(starElements[focusIndex], STAR_CLASSNAME + "-focus");
        }
        // update focusIndex and add class
        focusIndex = index;
        if (focusIndex >= 0 && focusIndex < starElements.length) {
            Element focusedStar = starElements[focusIndex];

            addClassName(focusedStar, STAR_CLASSNAME + "-focus");
            setBarWidth(calcBarWidth(focusedStar.getPropertyInt("rating")));
            VStarCaption.showAroundElement(focusedStar,
                    focusedStar.getPropertyString("caption"),
                    getCaptionId(focusedStar.getPropertyInt("rating")));
        }
    }

    private void changeFocusIndex(int delta) {
        int newFocusIndex = focusIndex + delta;

        // check for boundaries
        if (newFocusIndex >= 0 && newFocusIndex < starElements.length) {
            setFocusIndex(newFocusIndex);
        }
    }

    private void setValue(Element target) {
        if (target.getClassName().contains(STAR_CLASSNAME)) {
            int ratingValue = target.getPropertyInt("rating");
            value = ratingValue;
            client.updateVariable(uidlId, "value", ratingValue, immediate);
        }
    }

    private void addClassName(Element element, String className) {
        String currentClassName = element.getClassName();
        if (!currentClassName.contains(className)) {
            String newClassName = currentClassName + " " + className;
            element.setClassName(newClassName.trim());
        }
    }

    private void removeClassName(Element element, String classNameToRemove) {
        String currentClassName = element.getClassName();
        if (currentClassName.contains(classNameToRemove)) {
            String[] classNames = currentClassName.split(" ");
            String newClassName = "";
            for (String className : classNames) {
                if (!className.equals(classNameToRemove)) {
                    newClassName += className + " ";
                }
            }
            element.setClassName(newClassName);
        }
    }

    public void handleKeyUp(Event event) {
        if (event.getKeyCode() == KeyCodes.KEY_RIGHT) {
            changeFocusIndex(+1);
        } else if (event.getKeyCode() == KeyCodes.KEY_LEFT) {
            changeFocusIndex(-1);
        } else if (event.getKeyCode() == KeyCodes.KEY_ENTER) {
            setValue(starElements[focusIndex]);
        }
    }

    /**
     * Creates the DivElement of the bar representing the current value.
     * 
     * @return the newly created DivElement representing the bar.
     * @see #setBarWidth(byte)
     */
    private Element createBarDiv(int height) {
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

    public boolean isAnimationEnabled() {
        return animated;
    }

    public void setAnimationEnabled(boolean enable) {
        this.animated = enable;
    }

}

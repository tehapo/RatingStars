package org.vaadin.teemu.ratingstars.gwt.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;

public abstract class VStarCaption {

    public static final String STAR_CAPTION_ID = VRatingStars.STAR_CLASSNAME
            + "-caption";

    private static Element getStarCaption() {
        Document doc = Document.get();
        Element starCaption = doc.getElementById(STAR_CAPTION_ID);
        if (starCaption == null) {
            // caption element doesn't yet exist -> create
            starCaption = doc.createDivElement();

            Element starCaptionDecoration = doc.createDivElement();
            starCaption.appendChild(starCaptionDecoration);

            // span for the actual caption text
            Element starCaptionSpan = doc.createSpanElement();

            starCaption.setId(STAR_CAPTION_ID);
            starCaption.getStyle().setProperty("display", "none");
            starCaption.appendChild(starCaptionSpan);
            doc.getBody().appendChild(starCaption);
        }
        return starCaption;
    }

    public static void showAroundElement(Element target, String caption,
            String captionId) {
        if (caption != null) {
            Element starCaption = getStarCaption();

            // set the text and display element (to get width for calculations)
            starCaption.getElementsByTagName("span").getItem(0)
                    .setInnerText(caption);
            starCaption.setPropertyString("captionId", captionId);
            Style starCaptionStyle = starCaption.getStyle();
            starCaptionStyle.setProperty("display", "block");

            // calculate position
            int x = target.getAbsoluteLeft();
            x += (target.getClientWidth() / 2);
            x -= (starCaption.getClientWidth() / 2);
            int y = target.getAbsoluteTop();
            y += target.getClientHeight();

            // position the element
            starCaptionStyle.setProperty("left", x + "px");
            starCaptionStyle.setProperty("top", y + "px");
        } else {
            hide();
        }
    }

    public static boolean isVisibleForCaptionId(String captionId) {
        return isVisible()
                && getStarCaption().getPropertyString("captionId").equals(
                        captionId);
    }

    public static boolean isVisible() {
        return !("none".equals(getStarCaption().getStyle().getProperty(
                "display")));
    }

    public static void hide() {
        Style starCaptionStyle = getStarCaption().getStyle();
        starCaptionStyle.setProperty("display", "none");
        starCaptionStyle.setProperty("left", "-100px");
        starCaptionStyle.setProperty("top", "-100px");
    }

}

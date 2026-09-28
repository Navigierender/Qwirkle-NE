package quirkle.game.util;

import quirkle.engine.Entity;

import java.awt.*;

public class SimpleTextEntity extends Entity {
    public String message;
    OriginPresets textOrigin;
    float fontSize;
    Color textColor;
    String fontIdentifier;
    double textRotation;


    public SimpleTextEntity(String message, float fontSize, String fontIdentifier, Color textColor, OriginPresets textOrigin) {
        this.message = message;
        this.fontSize = fontSize;
        this.textOrigin = textOrigin;
        this.fontIdentifier = fontIdentifier;
        this.textColor = textColor;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onRender(Graphics2D g) {
        drawText(message, fontSize, textColor, fontIdentifier, x, y, textRotation, textOrigin, g);
    }

    /** Updates the displayed text without recreating the entity. */
    public void setMessage(String message) {
        this.message = message;
    }
}

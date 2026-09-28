package quirkle.game.util;

import quirkle.engine.*;

import java.awt.*;

public class RectangularButton extends Entity {

    public static final String[] BUTTONCLICK_PENTATONIC = {"btnclickpenta/B4","btnclickpenta/Cis5","btnclickpenta/E5","btnclickpenta/Fis5","btnclickpenta/Gis4"};

    String text;
    public double minSize = 1.0;
    double sizeModifier = minSize;
    public double maxSizeModifier = 1.1;
    public double growthSpeed = 0.01;

    public RectangularButton(String text) {
        this.text = text;
    }

    @Override
    public void onCreate() {
        origin = OriginPresets.CENTER;
        scale = minSize;
        setSprite("util/rectangularbutton/inactive");
        x = SceneManager.getCurrentScene().getCenterX();
    }

    @Override
    public void onTick(double dt) {
        if (isHovered() && sizeModifier <= maxSizeModifier) {
            if (sizeModifier <= maxSizeModifier) sizeModifier += growthSpeed;
            setSprite("util/rectangularbutton/active");

        }   else if (!isHovered() && sizeModifier >= minSize) {
            if (sizeModifier >= minSize) sizeModifier -= growthSpeed;
            setSprite("util/rectangularbutton/inactive");
        }

        scale = sizeModifier;
    }

    @Override
    public void onRender(Graphics2D g) {
        drawText(text, (float) (50*sizeModifier), Color.WHITE, "poly_regular", x, y-4, 0.0, OriginPresets.CENTER, g);
    }

    @Override
    public void onDestroy() {
        if (isHovered()) PlayRandomSound.playRand(RectangularButton.BUTTONCLICK_PENTATONIC);
    }
}

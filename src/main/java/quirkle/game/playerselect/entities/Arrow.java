package quirkle.game.playerselect.entities;

import quirkle.engine.*;

public class Arrow extends Entity {
    public double minSize = 1.0;
    double sizeModifier = minSize;
    public double maxSizeModifier = 1.1;
    public double growthSpeed = 0.01;

    @Override
    public void onCreate() {
        origin = OriginPresets.CENTER;
        scale = minSize;
        setSprite("playerselect/arrow/inactive");
        x = SceneManager.getCurrentScene().getCenterX();
    }

    @Override
    public void onTick(double dt) {
        if (isHovered() && sizeModifier <= maxSizeModifier) {
            if (sizeModifier <= maxSizeModifier) sizeModifier += growthSpeed;
            setSprite("playerselect/arrow/active");

        }   else if (!isHovered() && sizeModifier >= minSize) {
            if (sizeModifier >= minSize) sizeModifier -= growthSpeed;
            setSprite("playerselect/arrow/inactive");
        }

        scale = sizeModifier;
    }
}

package quirkle.game.debug;

import quirkle.engine.Entity;
import quirkle.engine.SceneManager;

public class DebugIndicator extends Entity {
    private static int borderSpacing = 60;

    public double minSize = 1.0;
    double sizeModifier = minSize;
    public double maxSizeModifier = 1.1;
    public double growthSpeed = 0.01;

    @Override
    public void onCreate() {
        setSprite("util/debugindicator/debugsymbol");
        origin = OriginPresets.CENTER;
        x = SceneManager.getCurrentScene().getWidth() - borderSpacing;
        y = 0 + borderSpacing;
    }

    @Override
    public void onTick(double dt) {
        if (isHovered() && sizeModifier <= maxSizeModifier) {
            if (sizeModifier <= maxSizeModifier) sizeModifier += growthSpeed;

        }   else if (!isHovered() && sizeModifier >= minSize) {
            if (sizeModifier >= minSize) sizeModifier -= growthSpeed;
        }

        scale = sizeModifier;
    }
}

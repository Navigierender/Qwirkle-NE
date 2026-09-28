package quirkle.game.settings.entities;

import quirkle.engine.Entity;
import quirkle.engine.InputManager;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * A draggable horizontal slider producing an int value in [0, 100].
 * @note {@link #x} refers to the slider's center (origin {@link OriginPresets#CENTER}),
 *       consistent with how most other entities in this codebase are positioned.
 * @note The track sprite doubles as the drag hitbox via {@link #setSprite}, matching
 *       how {@link quirkle.game.util.RectangularButton} uses its own sprite for hover detection.
 */
public class SoundVolumeSlider extends Entity {

    private final int initialValue;
    private int value;
    private boolean dragging;

    public SoundVolumeSlider(int initialValue) {
        this.initialValue = initialValue;
    }

    @Override
    public void onCreate() {
        origin = OriginPresets.CENTER;
        setSprite("settings/slider/track");
        value = clamp(initialValue);
    }

    @Override
    public void onTick(double dt) {
        if (!InputManager.isMousePressed()) {
            dragging = false;
            return;
        }

        if (!dragging && isHovered()) dragging = true;
        if (!dragging) return;

        double relativeX = InputManager.getMouseX() - getLeftEdgeX();
        double ratio = relativeX / getScaledWidth();
        value = clamp((int) Math.round(ratio * 100));
    }

    @Override
    public void onRender(Graphics2D g) {
        double handleX = getLeftEdgeX() + (value / 100.0) * getScaledWidth();
        drawSprite("settings/slider/handle", 1.0, handleX, y, 0.0, OriginPresets.CENTER, g);
        drawText(String.valueOf(value), 30f, Color.WHITE, "poly_regular", getLeftEdgeX() + getScaledWidth() + 40, y, 0.0, OriginPresets.CENTER_LEFT, g);
    }

    public int getValue() {
        return value;
    }

    /** the track's left edge in world space, derived from {@link #x} now that origin is {@link OriginPresets#CENTER} */
    private double getLeftEdgeX() {
        return x - getScaledWidth() / 2.0;
    }

    private static int clamp(int rawValue) {
        return Math.max(0, Math.min(100, rawValue));
    }
}
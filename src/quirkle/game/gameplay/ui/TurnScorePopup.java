package quirkle.game.gameplay.ui;

import quirkle.engine.Entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/** Shows the points of a turn in the middle of the board for a short time */
public class TurnScorePopup extends Entity {

    /** part of the animation where the text is fully visible before fading out */
    private static final double HOLD_UNTIL = 0.5;

    private final int centerX;
    private final int centerY;

    private int points;

    private double remaining = 0.0;

    public TurnScorePopup(Rectangle boardViewport) {
        this.centerX = (int) boardViewport.getCenterX();
        this.centerY = (int) boardViewport.getCenterY();
        this.renderOrder = UiTheme.LAYER_HUD;
    }

    /** starts the popup, nothing is shown for 0 points */
    public void show(int points) {
        if (points <= 0) return;

        this.points = points;
        this.remaining = UiTheme.SCORE_POPUP_SECONDS;
    }

    @Override
    public void onTick(double dt) {
        if (remaining > 0.0) remaining = Math.max(0.0, remaining - dt);
    }

    @Override
    public void onRender(Graphics2D g) {
        if (remaining <= 0.0) return;

        double progress = 1.0 - remaining / UiTheme.SCORE_POPUP_SECONDS;

        drawText("+" + points, UiTheme.FONT_SIZE_SCORE_POPUP, fadedText(progress), UiTheme.FONT_SCORE,
                centerX, centerY - rise(progress), 0.0, OriginPresets.CENTER, g);
    }

    /** moves the number up, fast at first and then slower */
    private static double rise(double progress) {
        double remainingRise = 1.0 - progress;
        return UiTheme.SCORE_POPUP_RISE * (1.0 - remainingRise * remainingRise);
    }

    private static Color fadedText(double progress) {
        double alpha = progress < HOLD_UNTIL ? 1.0 : (1.0 - progress) / (1.0 - HOLD_UNTIL);

        return new Color(UiTheme.TEXT.getRed(), UiTheme.TEXT.getGreen(), UiTheme.TEXT.getBlue(),
                (int) Math.round(Math.clamp(alpha, 0.0, 1.0) * 255));
    }
}

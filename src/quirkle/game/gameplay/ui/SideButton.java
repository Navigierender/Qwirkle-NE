package quirkle.game.gameplay.ui;

import quirkle.engine.InputManager;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class SideButton extends PanelEntity {

    private static final int PLACEHOLDER_CORNER_RADIUS = 10;
    private static final float DISABLED_ALPHA = 0.35f;

    private final String label;

    private Runnable action;

    private boolean enabled = true;

    public SideButton(String label, Runnable action) {
        this.label = label;
        this.action = action;
    }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public void setAction(Runnable action) { this.action = action; }

    public void place(int rightX, int centerY) {
        setBounds(rightX - UiTheme.SIDE_BUTTON_WIDTH / 2, centerY,
                UiTheme.SIDE_BUTTON_WIDTH, UiTheme.SIDE_BUTTON_HEIGHT, OriginPresets.CENTER);
    }

    public boolean handleInput() {
        if (!InputManager.isMouseClicked() || !isHovered()) return false;

        if (enabled && action != null) action.run();
        return true;
    }

    @Override
    public void onRender(Graphics2D g) {
        Graphics2D gButton = (Graphics2D) g.create();
        if (!enabled) {
            gButton.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, DISABLED_ALPHA));
        }

        drawBody(gButton);
        drawCaption(gButton);

        gButton.dispose();
    }

    private void drawBody(Graphics2D g) {
        g.setColor(UiTheme.PLACEHOLDER_FILL);
        g.fillRoundRect(getLeft(), getTop(), getWidth(), getHeight(),
                PLACEHOLDER_CORNER_RADIUS, PLACEHOLDER_CORNER_RADIUS);

        g.setColor(UiTheme.PLACEHOLDER_BORDER);
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(getLeft(), getTop(), getWidth(), getHeight(),
                PLACEHOLDER_CORNER_RADIUS, PLACEHOLDER_CORNER_RADIUS);
    }

    private void drawCaption(Graphics2D g) {
        if (label == null) return;

        Color color = enabled && isHovered() ? UiTheme.GOLD : UiTheme.TEXT;

        drawText(label, UiTheme.FONT_SIZE_BUTTON, color, UiTheme.FONT,
                getLeft() + getWidth() / 2, getTop() + getHeight() / 2, 0.0, OriginPresets.CENTER, g);
    }
}

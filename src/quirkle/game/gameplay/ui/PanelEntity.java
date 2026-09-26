package quirkle.game.gameplay.ui;

import quirkle.engine.Entity;

public abstract class PanelEntity extends Entity {

    protected PanelEntity() {
        this.renderOrder = UiTheme.LAYER_HUD;
    }

    protected void setBounds(int x, int y, int width, int height, OriginPresets origin) {
        this.origin = origin;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getLeft() {
        return (int) (x - origin.x * getScaledWidth());
    }

    public int getTop() {
        return (int) (y - origin.y * getScaledHeight());
    }

    public int getWidth() {
        return (int) getScaledWidth();
    }

    public int getHeight() {
        return (int) getScaledHeight();
    }
}

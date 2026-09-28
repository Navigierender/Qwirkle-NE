package quirkle.game.gameplay.tiles;

import quirkle.engine.Entity;
import quirkle.engine.InputManager;
import quirkle.game.gameplay.ui.UiTheme;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public abstract class TileEntity extends Entity {
    protected static final float OUTLINE_WIDTH = 3f;

    private final Tile tile;

    private double tileSize;

    protected TileEntity(Tile tile) {
        this.tile = tile;
        this.origin = OriginPresets.CENTER;
    }

    @Override
    public void onCreate() {
        setSprite(buildSpritePath(tile));
    }

    public Tile getTile() { return tile; }

    public double getTileSize() { return tileSize; }

    public void setTileSize(double tileSize) {
        this.tileSize = tileSize;

        double artworkSize = tileSize * (1.0 - 2.0 * UiTheme.TILE_PADDING_RATIO);
        this.scale = artworkSize / Math.max(width, height);
    }

    protected int getBoxX() { return (int) (x - origin.x * tileSize); }

    protected int getBoxY() { return (int) (y - origin.y * tileSize); }

    protected void drawOutline(Graphics2D g, Color color) {
        int inset = (int) (OUTLINE_WIDTH / 2f);
        int size = (int) tileSize - 2 * inset - 1;

        Graphics2D gOutline = (Graphics2D) g.create();
        gOutline.setColor(color);
        gOutline.setStroke(new BasicStroke(OUTLINE_WIDTH));
        gOutline.drawRect(getBoxX() + inset, getBoxY() + inset, size, size);
        gOutline.dispose();
    }

    @Override
    public boolean isHovered() {
        double mouseX = InputManager.getMouseX();
        double mouseY = InputManager.getMouseY();

        int left = getBoxX();
        int top = getBoxY();

        return mouseX >= left && mouseX <= left + tileSize
            && mouseY >= top && mouseY <= top + tileSize;
    }

    private String buildSpritePath(Tile tile) {
        String shape = tile.getSymbol().name().toLowerCase();   // "hexagon"
        String color = tile.getColor().name().toLowerCase();    // "red"
        return "gameplay/tiles/" + shape + "/" + shape + "_" + color;
    }
}

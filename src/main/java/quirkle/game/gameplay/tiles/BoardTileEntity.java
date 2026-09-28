package quirkle.game.gameplay.tiles;

import quirkle.game.gameplay.board.Position;

import java.awt.Color;
import java.awt.Graphics2D;

public class BoardTileEntity extends TileEntity {
    private static final Color PENDING_COLOR = new Color(255, 255, 255, 140);

    private final Position position;

    private boolean pending = false;

    public BoardTileEntity(Tile tile, Position position) {
        super(tile);
        this.position = position;
    }

    public Position getPosition() { return position; }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    @Override
    public void onRender(Graphics2D g) {
        if (!pending) return;

        drawOutline(g, PENDING_COLOR);
    }
}

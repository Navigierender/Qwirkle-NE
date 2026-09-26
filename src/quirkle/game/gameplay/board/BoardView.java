package quirkle.game.gameplay.board;

import quirkle.engine.EngineConfig;
import quirkle.engine.Entity;
import quirkle.game.gameplay.tiles.BoardTileEntity;
import quirkle.game.gameplay.tiles.Tile;
import quirkle.game.gameplay.ui.UiTheme;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedHashMap;
import java.util.Map;

public class BoardView extends Entity {

    private final BoardCamera camera;
    private BoardGrid grid;

    private final Rectangle viewport;

    private final Map<Position, BoardTileEntity> tileEntities = new LinkedHashMap<>();

    private BoardTileEntity previewTile;

    public BoardView(BoardCamera camera, Rectangle viewport) {
        this.camera = camera;
        this.viewport = viewport;
        this.renderOrder = UiTheme.LAYER_GRID;
    }

    @Override
    public void onCreate() {
        // the grid covers the whole window, the frame on top hides the rest.
        // viewport is still only the opening because clicks outside of it are ignored
        grid = new BoardGrid(camera, windowBounds());
        grid.create();
    }

    private static Rectangle windowBounds() {
        return new Rectangle(0, 0, EngineConfig.WINDOW_WIDTH, EngineConfig.WINDOW_HEIGHT);
    }

    public boolean contains(int screenX, int screenY) {
        return viewport.contains(screenX, screenY);
    }

    /** Shows a transparent preview of the tile on the position. null removes the preview */
    public void setPlacementPreview(Tile tile, Position position) {
        if (tile == null || position == null) {
            previewTile = null;
            return;
        }

        // only create a new preview if the tile or position changed
        if (previewTile != null && previewTile.getTile() == tile
                && previewTile.getPosition().equals(position)) {
            return;
        }

        previewTile = new BoardTileEntity(tile, position);
        previewTile.create();
        layoutTile(previewTile, camera.getTileSize());
    }

    public void sync(Board board) {
        addMissingEntities(board.getPlacedTiles());
        addMissingEntities(board.getPendingTiles());
        removeTakenBackEntities(board);
        layoutTiles(board);
    }

    @Override
    public void onTick(double dt) {
        grid.update(dt);

        for (BoardTileEntity tileEntity : tileEntities.values()) {
            tileEntity.update(dt);
        }

        // keep the preview on its cell when the camera moves
        if (previewTile != null) layoutTile(previewTile, camera.getTileSize());
    }

    @Override
    public void onRender(Graphics2D g) {
        // no clipping, the frame is drawn on top and hides the rest
        grid.render(g);

        for (BoardTileEntity tileEntity : tileEntities.values()) {
            tileEntity.render(g);
        }

        drawPlacementPreview(g);
    }

    private void drawPlacementPreview(Graphics2D g) {
        if (previewTile == null) return;

        Graphics2D gPreview = (Graphics2D) g.create();
        gPreview.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                UiTheme.PLACEMENT_PREVIEW_OPACITY));
        previewTile.render(gPreview);
        gPreview.dispose();
    }

    @Override
    public void onDestroy() {
        grid.destroy();

        for (BoardTileEntity tileEntity : tileEntities.values()) {
            tileEntity.destroy();
        }
        tileEntities.clear();
    }

    private void layoutTiles(Board board) {
        double tileSize = camera.getTileSize();
        Map<Position, Tile> pendingTiles = board.getPendingTiles();

        for (BoardTileEntity tileEntity : tileEntities.values()) {
            layoutTile(tileEntity, tileSize);
            tileEntity.setPending(pendingTiles.containsKey(tileEntity.getPosition()));
        }
    }

    private void layoutTile(BoardTileEntity tileEntity, double tileSize) {
        Point screenPos = camera.boardToScreen(tileEntity.getPosition());
        tileEntity.x = screenPos.x;
        tileEntity.y = screenPos.y;
        tileEntity.setTileSize(tileSize);
    }

    private void addMissingEntities(Map<Position, Tile> tiles) {
        for (Map.Entry<Position, Tile> entry : tiles.entrySet()) {
            Position position = entry.getKey();
            if (tileEntities.containsKey(position)) continue;

            BoardTileEntity tileEntity = new BoardTileEntity(entry.getValue(), position);
            tileEntity.create();
            tileEntities.put(position, tileEntity);
        }
    }

    private void removeTakenBackEntities(Board board) {
        tileEntities.entrySet().removeIf(entry -> {
            Position position = entry.getKey();
            if (board.getPlacedTiles().containsKey(position)) return false;
            if (board.getPendingTiles().containsKey(position)) return false;

            entry.getValue().destroy();
            return true;
        });
    }
}

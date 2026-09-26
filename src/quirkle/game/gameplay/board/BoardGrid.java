package quirkle.game.gameplay.board;

import quirkle.engine.Entity;
import quirkle.game.gameplay.ui.UiTheme;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Path2D;

public class BoardGrid extends Entity {

    private static final float LINE_WIDTH = 1f;
    private static final double MARKER_RATIO = 0.22;
    private static final int OVERSCAN = 1;

    private final BoardCamera camera;

    private final Rectangle viewport;

    public BoardGrid(BoardCamera camera, Rectangle viewport) {
        this.camera = camera;
        this.viewport = viewport;
        this.renderOrder = UiTheme.LAYER_GRID;
    }

    @Override
    public void onRender(Graphics2D g) {
        double cellSize = camera.getTileSize();
        if (cellSize <= 0) return;

        Position topLeft = camera.screenToBoard(viewport.x, viewport.y);
        Position bottomRight = camera.screenToBoard(viewport.x + viewport.width, viewport.y + viewport.height);

        int firstColumn = topLeft.x() - OVERSCAN;
        int lastColumn = bottomRight.x() + OVERSCAN;
        int firstRow = topLeft.y() - OVERSCAN;
        int lastRow = bottomRight.y() + OVERSCAN;

        drawLines(g, cellSize, firstColumn, lastColumn, firstRow, lastRow);
        drawMarkers(g, cellSize, firstColumn, lastColumn, firstRow, lastRow);
    }

    private void drawLines(Graphics2D g, double cellSize, int firstColumn, int lastColumn, int firstRow, int lastRow) {
        Graphics2D gLines = (Graphics2D) g.create();
        gLines.setColor(UiTheme.GRID_LINE);
        gLines.setStroke(new BasicStroke(LINE_WIDTH));

        int top = cornerY(firstRow, cellSize);
        int bottom = cornerY(lastRow + 1, cellSize);
        int left = cornerX(firstColumn, cellSize);
        int right = cornerX(lastColumn + 1, cellSize);

        for (int column = firstColumn; column <= lastColumn + 1; column++) {
            int lineX = cornerX(column, cellSize);
            gLines.drawLine(lineX, top, lineX, bottom);
        }

        for (int row = firstRow; row <= lastRow + 1; row++) {
            int lineY = cornerY(row, cellSize);
            gLines.drawLine(left, lineY, right, lineY);
        }

        gLines.dispose();
    }

    private void drawMarkers(Graphics2D g, double cellSize, int firstColumn, int lastColumn, int firstRow, int lastRow) {
        Graphics2D gMarkers = (Graphics2D) g.create();
        gMarkers.setColor(UiTheme.GRID_MARKER);
        gMarkers.setStroke(new BasicStroke(LINE_WIDTH * 2f));

        double markerRadius = cellSize * MARKER_RATIO / 2.0;

        for (int column = firstColumn; column <= lastColumn + 1; column++) {
            for (int row = firstRow; row <= lastRow + 1; row++) {
                gMarkers.draw(buildDiamond(cornerX(column, cellSize), cornerY(row, cellSize), markerRadius));
            }
        }

        gMarkers.dispose();
    }

    private Path2D.Double buildDiamond(int centerX, int centerY, double radius) {
        Path2D.Double diamond = new Path2D.Double();
        diamond.moveTo(centerX, centerY - radius);
        diamond.lineTo(centerX + radius, centerY);
        diamond.lineTo(centerX, centerY + radius);
        diamond.lineTo(centerX - radius, centerY);
        diamond.closePath();
        return diamond;
    }

    private int cornerX(int column, double cellSize) {
        return (int) (camera.boardToScreen(new Position(column, 0)).x - cellSize / 2.0);
    }

    private int cornerY(int row, double cellSize) {
        return (int) (camera.boardToScreen(new Position(0, row)).y - cellSize / 2.0);
    }
}

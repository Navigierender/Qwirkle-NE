package quirkle.game.gameplay.board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import quirkle.game.gameplay.tiles.Tile;

/** Sparse-grid game board, split into committed ({@link #placedTiles}) and not-yet-committed ({@link #pendingTiles}) tiles. */
public class Board {
    /** Tiles committed to the board. */
    private final Map<Position, Tile> placedTiles;

    /** Tiles placed this turn but not committed. */
    private final Map<Position, Tile> pendingTiles;

    public Board() {
        this.placedTiles = new HashMap<>();
        this.pendingTiles = new HashMap<>();
    }

    /**
     * Creates a board that already holds tiles, for setting a position up in one go.
     */
    public Board(Map<Position, Tile> placedTiles, Map<Position, Tile> pendingTiles) {
        this.placedTiles = new HashMap<>(placedTiles);
        this.pendingTiles = new HashMap<>(pendingTiles);
    }

    public Map<Position, Tile> getPlacedTiles() {
        return Collections.unmodifiableMap(placedTiles);
    }

    public Map<Position, Tile> getPendingTiles() {
        return Collections.unmodifiableMap(pendingTiles);
    }

    /**
     * @return the tile on {@code position}, or {@code null} if the cell is empty
     */
    public Tile getTileAt(Position position) {
        Tile pendingTile = pendingTiles.get(position);
        return pendingTile != null ? pendingTile : placedTiles.get(position);
    }

    /** @return if {@code position} holds a tile, committed or placed. */
    public boolean isOccupied(Position position) {
        return getTileAt(position) != null;
    }

    /**
     * @return smallest x-coordinate holding a committed tile
     */
    public int minX() {
        int min = 0;
        for (Position position : placedTiles.keySet()) {
            if (position.x() < min) {
                min = position.x();
            }
        }
        return min;
    }

    /**
     * @return largest x-coordinate holding a committed tile
     */
    public int maxX() {
        int max = 0;
        for (Position position : placedTiles.keySet()) {
            if (position.x() > max) {
                max = position.x();
            }
        }
        return max;
    }

    /**
     * @return smallest y-coordinate holding a committed tile
     */
    public int minY() {
        int min = 0;
        for (Position position : placedTiles.keySet()) {
            if (position.y() < min) {
                min = position.y();
            }
        }
        return min;
    }

    /**
     * @return largest y-coordinate holding a committed tile
     */
    public int maxY() {
        int max = 0;
        for (Position position : placedTiles.keySet()) {
            if (position.y() > max) {
                max = position.y();
            }
        }
        return max;
    }

    /** Clears placed and pending tiles */
    public void clear() {
        this.placedTiles.clear();
        this.pendingTiles.clear();
    }

    /**
     * Stages {@code tile} at {@code position} as a pending tile for the current turn.
     *
     * @throws IllegalStateException if {@code position} is already occupied by a placed or pending tile, or if placing {@code tile} there would make the pending tiles illegal.
     */
    public void placeTile(Position position, Tile tile) {
        PlacementResult result = checkPlacement(position, tile);

        if (!result.isLegal()) {
            throw new IllegalStateException("Cannot place tile; " + result.getDescription() + ".");
        }

        pendingTiles.put(position, tile);
    }

    public void placeTiles(Map<Position, Tile> tiles) {
        Map<Position, Tile> candidate = new HashMap<>(pendingTiles);
        candidate.putAll(tiles);

        PlacementResult result = PlacementValidator.checkPendingTilePlacement(placedTiles, candidate);
        if (!result.isLegal()) throw new IllegalStateException("Cannot place tiles; " + result.getDescription() + ".");

        pendingTiles.putAll(tiles);
    }

    /**
     * Takes the pending tile at {@code position} off the board.
     *
     * @return the tile that has been removed
     */
    public Tile removePendingTile(Position position) {
        return pendingTiles.remove(position);
    }

    /**
     * Takes every pending tile off the board.
     *
     * @return the tiles that were staged
     */
    public List<Tile> removeAllPendingTiles() {
        List<Tile> removed = new ArrayList<>(pendingTiles.values());
        pendingTiles.clear();
        return removed;
    }

    /**
     * @return whether the tiles staged so far could be committed as they stand
     * @note also true if nothing is placed
     */
    public boolean isPendingPlacementLegal() {
        return checkPendingPlacement().isLegal();
    }

    /**
     * Commits all pending tiles and returns the points scored.
     *
     * @throws IllegalStateException if a pending tile's position is already occupied
     */
    public int commitPendingTiles() {
        PlacementResult result = PlacementValidator.checkPendingTilePlacement(placedTiles, pendingTiles);

        if (!result.isLegal()) {
            throw new IllegalStateException("Cannot commit pending tiles; " + result.getDescription() + ".");
        }

        int points = ScoreCalculator.calculatePendingTilesScore(placedTiles, pendingTiles);
        placedTiles.putAll(pendingTiles);
        pendingTiles.clear();
        return points;
    }

    /**
     * Checks if {@code tile} could be staged at {@code position} right now, without staging it.
     *
     * @return {@link PlacementResult#LEGAL}, or the rule that would reject the placement
     */
    public PlacementResult checkPlacement(Position position, Tile tile) {
        return PlacementValidator.checkTilePlacement(placedTiles, pendingTiles, position, tile);
    }

    /**
     * @return {@link PlacementResult#LEGAL} if the tiles staged so far could be committed, or the rule that stops them
     */
    public PlacementResult checkPendingPlacement() {
        if (pendingTiles.isEmpty()) return PlacementResult.LEGAL;
        return PlacementValidator.checkPendingTilePlacement(placedTiles, pendingTiles);
    }

    /**
     * @return the points the staged tiles would earn if the turn ended now, {@code 0} if nothing is staged
     */
    public int getPendingScore() {
        if (pendingTiles.isEmpty()) return 0;
        return ScoreCalculator.calculatePendingTilesScore(placedTiles, pendingTiles);
    }
}

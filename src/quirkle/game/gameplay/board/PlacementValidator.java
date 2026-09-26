package quirkle.game.gameplay.board;

import java.util.*;

import quirkle.game.gameplay.tiles.Tile;
import quirkle.game.gameplay.tiles.TileColor;
import quirkle.game.gameplay.tiles.TileSymbol;

class PlacementValidator {

    /**
     * @return is placement for {@code pendingTiles} onto {@code placedTiles} possible
     */
    static boolean isPendingTilePlacementPossible(Map<Position, Tile> placedTiles, Map<Position, Tile> pendingTiles) {
        return checkPendingTilePlacement(placedTiles, pendingTiles).isLegal();
    }

    /**
     * Checks {@code pendingTiles} against every placement rule
     * @return {@link PlacementResult#LEGAL}, or the first rule that rejects them
     */
    static PlacementResult checkPendingTilePlacement(Map<Position, Tile> placedTiles, Map<Position, Tile> pendingTiles) {
        for (Position position : pendingTiles.keySet()) {
            if (!isPositionFree(placedTiles, position)) return PlacementResult.POSITION_OCCUPIED;
        }
        if (!isPendingTilesInLine(pendingTiles.keySet())) return PlacementResult.NOT_IN_ONE_LINE;
        if (!isPendingTilesContiguous(pendingTiles)) return PlacementResult.LINE_HAS_GAP;
        if (!isConnectedToBoard(placedTiles, pendingTiles)) return PlacementResult.NOT_CONNECTED_TO_BOARD;

        Map<Position, Tile> allTiles = new HashMap<>(placedTiles);
        allTiles.putAll(pendingTiles);

        for (Position position : pendingTiles.keySet()) {
            if (!formsValidLines(allTiles, position)) return PlacementResult.INVALID_LINE;
        }
        return PlacementResult.LEGAL;
    }

    static PlacementResult checkTilePlacement(Map<Position, Tile> placedTiles, Map<Position, Tile> pendingTiles, Position position, Tile tile) {
        if (placedTiles.containsKey(position) || pendingTiles.containsKey(position)) return PlacementResult.POSITION_OCCUPIED;

        Map<Position, Tile> candidatePendingTiles = new HashMap<>(pendingTiles);
        candidatePendingTiles.put(position, tile);
        return checkPendingTilePlacement(placedTiles, candidatePendingTiles);
    }

    /** @return every tile shares the same row or the same column */
    private static boolean isPendingTilesInLine(Set<Position> positions) {
        boolean sameRow = positions.stream().map(Position::y).distinct().count() == 1;
        boolean sameColumn = positions.stream().map(Position::x).distinct().count() == 1;
        return sameRow || sameColumn;
    }

    /**
     * @return pending tiles form one unbroken run
     * @apiNote Assumes {@link #isPendingTilesInLine} has already passed
     */
    private static boolean isPendingTilesContiguous(Map<Position, Tile> pendingTiles) {
        LineOrientation orientation = getPendingTilesOrientation(pendingTiles.keySet());
        if (orientation == null) {
            return true;
        }

        int lineEnd = pendingTiles.keySet().stream().mapToInt(orientation.extentCoordinate).max().getAsInt();
        Position current = pendingTiles.keySet().stream()
                .min(Comparator.comparingInt(orientation.extentCoordinate))
                .orElseThrow();

        // walk from the first to the last pending tile; every position on the way must hold a tile
        while (orientation.extentCoordinate.applyAsInt(current) <= lineEnd) {
            if (!pendingTiles.containsKey(current)) {
                return false;
            }
            current = current.neighbor(orientation.forwardDirection);
        }
        return true;
    }

    /** @return the orientation {@code positions} run in, or {@code null} if a single tile leaves it undecided. */
    private static LineOrientation getPendingTilesOrientation(Set<Position> positions) {
        if (positions.size() <= 1) {
            return null;
        }

        boolean sameRow = positions.stream().map(Position::y).distinct().count() == 1;
        return sameRow ? LineOrientation.HORIZONTAL : LineOrientation.VERTICAL;
    }

    /** @return if a pending tile touches the board, or the board is still empty (first move). */
    private static boolean isConnectedToBoard(Map<Position, Tile> placedTiles, Map<Position, Tile> pendingTiles) {
        if (placedTiles.isEmpty()) {
            return true;
        }
        for (Position position : pendingTiles.keySet()) {
            if (tileHasNeighbor(placedTiles, position)) {
                return true;
            }
        }
        return false;
    }

    /** @return if {@code position} has at least one occupied neighbor */
    private static boolean tileHasNeighbor(Map<Position, Tile> tiles, Position position) {
        for (Direction direction : Direction.values()) {
            if (tiles.containsKey(position.neighbor(direction))) {
                return true;
            }
        }
        return false;
    }

    /** @return if {@code position} is free in {@code placedTiles} */
    private static boolean isPositionFree(Map<Position, Tile> placedTiles, Position position) {
        return !placedTiles.containsKey(position);
    }


    /** @return if both lines through {@code position} are valid Qwirkle lines */
    private static boolean formsValidLines(Map<Position, Tile> tiles, Position position) {
        for (LineOrientation orientation : LineOrientation.values()) {
            if (!isValidLine(collectLine(tiles, position, orientation))) return false;
        }
        return true;
    }

    /** @return every tile contiguously connected to {@code position} along {@code orientation} */
    private static List<Tile> collectLine(Map<Position, Tile> tiles, Position position, LineOrientation orientation) {
        Position lineStart = StreakWalker.walkToStreakEnd(tiles, position, orientation.backwardDirection);
        Position lineEnd = StreakWalker.walkToStreakEnd(tiles, position, orientation.forwardDirection);
        int length = orientation.extentCoordinate.applyAsInt(lineEnd) - orientation.extentCoordinate.applyAsInt(lineStart) + 1;

        List<Tile> line = new ArrayList<>(length);
        Position current = lineStart;

        for (int i = 0; i < length; i++) {
            line.add(tiles.get(current));
            current = current.neighbor(orientation.forwardDirection);
        }

        return line;
    }

    /**
     * @return if {@code line} holds at most one tile, or shares one color with distinct symbols,
     *         or one symbol with distinct colors
     */
    private static boolean isValidLine(List<Tile> line) {
        if (line.size() <= 1) return true;

        Set<TileColor> colors = new HashSet<>();
        Set<TileSymbol> symbols = new HashSet<>();
        for (Tile tile : line) {
            colors.add(tile.getColor());
            symbols.add(tile.getSymbol());
        }

        boolean allSameColor = colors.size() == 1;
        boolean allSameSymbol = symbols.size() == 1;
        boolean allColorsDifferent = colors.size() == line.size();
        boolean allSymbolsDifferent = symbols.size() == line.size();

        return (allSameColor && allSymbolsDifferent) || (allSameSymbol && allColorsDifferent);
    }
}

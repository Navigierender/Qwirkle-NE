package quirkle.game.gameplay.board;

import quirkle.game.gameplay.tiles.Tile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class ScoreCalculator {

    private static final int QWIRKLE_LINE_LENGTH = 6;
    private static final int QWIRKLE_BONUS = 6;

    /**
     * Scores the pending tiles: one point per tile in every line they form or extend, plus a
     * {@value QWIRKLE_BONUS}-point bonus per completed six-tile line.
     *
     * @return points earned for the board's pending tiles
     * @note A tile at a crossing point scores both its row and its column.
     * @apiNote Assumes {@code pendingTiles} are validated by {@link PlacementValidator}
     */
    static int calculatePendingTilesScore(Map<Position, Tile> placedTiles, Map<Position, Tile> pendingTiles) {
        Map<Position, Tile> allTiles = mergeTiles(placedTiles, pendingTiles);
        List<Integer> formedLineLengths = findFormedLineLengths(allTiles, pendingTiles);

        if (formedLineLengths.isEmpty()) {
            // first move with only one tile
            return pendingTiles.size();
        }
        return sumWithQwirkleBonus(formedLineLengths);
    }

    /** @return one entry per line the pending tiles form or extend, holding its length. */
    private static List<Integer> findFormedLineLengths(Map<Position, Tile> allTiles, Map<Position, Tile> pendingTiles) {
        List<Integer> lineLengths = new ArrayList<>();
        for (LineOrientation orientation : LineOrientation.values()) {
            lineLengths.addAll(lineLengthsForOrientation(allTiles, pendingTiles, orientation));
        }
        return lineLengths;
    }

    /** @return the length of each line, in {@code orientation}, that the pending tiles form or extend. */
    private static List<Integer> lineLengthsForOrientation(Map<Position, Tile> allTiles, Map<Position, Tile> pendingTiles, LineOrientation orientation) {
        List<Integer> lineLengths = new ArrayList<>();
        for (int lineKey : distinctLineKeys(pendingTiles.keySet(), orientation)) {
            int lineLength = calculateLineLength(allTiles, pendingTiles, orientation, lineKey);
            if (lineLength >= 2) {
                lineLengths.add(lineLength);
            }
        }
        return lineLengths;
    }

    /** @return length of the streak on {@code lineKey} */
    private static int calculateLineLength(Map<Position, Tile> allTiles, Map<Position, Tile> pendingTiles, LineOrientation orientation, int lineKey) {
        List<Position> pendingOnLine = pendingTilesOnLine(pendingTiles.keySet(), orientation, lineKey);

        Position innermostStart = earliestPendingPosition(pendingOnLine, orientation);
        Position innermostEnd = latestPendingPosition(pendingOnLine, orientation);

        Position lineStart = StreakWalker.walkToStreakEnd(allTiles, innermostStart, orientation.backwardDirection);
        Position lineEnd = StreakWalker.walkToStreakEnd(allTiles, innermostEnd, orientation.forwardDirection);

        return orientation.extentCoordinate.applyAsInt(lineEnd) - orientation.extentCoordinate.applyAsInt(lineStart) + 1;
    }

    /** @return the distinct lines {@code positions} touch, named by their fixed coordinate */
    private static Set<Integer> distinctLineKeys(Set<Position> positions, LineOrientation orientation) {
        Set<Integer> lineKeys = new HashSet<>();
        for (Position position : positions) {
            lineKeys.add(orientation.fixedCoordinate.applyAsInt(position));
        }
        return lineKeys;
    }

    /** @return the pending positions lying on {@code lineKey} */
    private static List<Position> pendingTilesOnLine(Set<Position> pendingPositions, LineOrientation orientation, int lineKey) {
        List<Position> pendingOnLine = new ArrayList<>();
        for (Position position : pendingPositions) {
            if (orientation.fixedCoordinate.applyAsInt(position) == lineKey) {
                pendingOnLine.add(position);
            }
        }
        return pendingOnLine;
    }

    /** @return the sum of {@code lineLengths}, plus {@value QWIRKLE_BONUS} per six-tile line */
    private static int sumWithQwirkleBonus(List<Integer> lineLengths) {
        int score = 0;
        for (int lineLength : lineLengths) {
            score += lineLength;
            if (lineLength == QWIRKLE_LINE_LENGTH) {
                score += QWIRKLE_BONUS;
            }
        }
        return score;
    }

    /** @return the pending position closest to the line's backward end */
    private static Position earliestPendingPosition(List<Position> pendingOnLine, LineOrientation orientation) {
        return pendingOnLine.stream().min(Comparator.comparingInt(orientation.extentCoordinate)).orElseThrow();
    }

    /** @return the pending position closest to the line's forward end */
    private static Position latestPendingPosition(List<Position> pendingOnLine, LineOrientation orientation) {
        return pendingOnLine.stream().max(Comparator.comparingInt(orientation.extentCoordinate)).orElseThrow();
    }

    /** @return a new map holding all entries of both, {@code second} winning on collisions */
    private static Map<Position, Tile> mergeTiles(Map<Position, Tile> first, Map<Position, Tile> second) {
        Map<Position, Tile> merged = new HashMap<>(first);
        merged.putAll(second);
        return merged;
    }
}

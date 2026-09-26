package quirkle.game.gameplay.board;

import quirkle.game.gameplay.tiles.Tile;
import quirkle.game.gameplay.tiles.TileColor;
import quirkle.game.gameplay.tiles.TileSymbol;
import quirkle.testing.Test;

import java.util.HashMap;
import java.util.Map;

import static quirkle.testing.Assertions.assertEquals;

public class ScoreCalculatorTest {

    @Test
    void testSinglePendingTileWithoutNeighbors() {
        Board board = new Board(tilesAt(), tilesAt(at(0, 0)));

        assertScore("Single tile without neighbors (first move)", 1, board);
    }

    @Test
    void testPendingTilesFormVerticalLine() {
        Board board = new Board(tilesAt(), tilesAt(at(2, 1), at(2, 2), at(2, 3)));

        assertScore("Three new tiles form a column (same x)", 3, board);
    }

    @Test
    void testPendingTilesFormHorizontalLine() {
        Board board = new Board(tilesAt(), tilesAt(at(1, 5), at(2, 5), at(3, 5)));

        assertScore("Three new tiles form a row (same y)", 3, board);
    }

    @Test
    void testPendingTileExtendsExistingLine() {
        Board board = new Board(tilesAt(at(2, 3), at(2, 4)), tilesAt(at(2, 5)));

        assertScore("New tile extends an existing column", 3, board);
    }

    @Test
    void testGapInPlacedTilesBreaksTheStreak() {
        Board board = new Board(
                tilesAt(at(2, 1) /* through a gap, not adjacent */, at(2, 4) /* adjacent to the pending tile */),
                tilesAt(at(2, 5)));

        assertScore("A gap breaks the contiguous streak", 2, board);
    }

    @Test
    void testMultiplePendingTilesInSameLineAreCountedOnce() {
        Board board = new Board(tilesAt(at(2, 0)), tilesAt(at(2, 1), at(2, 2)));

        // One column of 3 tiles (2,0)-(2,2) -> 3 points, not e.g. counted twice.
        assertScore("Multiple new tiles in the same column are counted once", 3, board);
    }

    @Test
    void testPendingTileAtCrossingScoresBothLines() {
        Board board = new Board(
                tilesAt(at(0, 5), at(1, 5), at(2, 2), at(2, 3), at(2, 4)),
                tilesAt(at(2, 5)));

        // Horizontal row (0,5)-(2,5): 3 points. Vertical column (2,2)-(2,5): 4 points. Total: 7.
        assertScore("A crossing tile scores both its row and its column", 7, board);
    }

    @Test
    void testMultiplePendingTilesEachFormOwnSecondaryLine() {
        Board board = new Board(
                tilesAt(at(1, 4) /* secondary line for (1,5) */, at(2, 6) /* secondary line for (2,5) */),
                tilesAt(at(1, 5), at(2, 5), at(3, 5)));

        // Main row (1,5)-(3,5): 3 points. Secondary line (1,4)-(1,5): 2 points. Secondary line (2,5)-(2,6): 2 points. Total: 7.
        assertScore("Main row plus secondary lines from multiple new tiles", 7, board);
    }

    @Test
    void testMultiplePendingTilesExtendExistingRowAndColumn() {
        Board board = new Board(
                tilesAt(at(0, 5) /* existing row, left of the new tiles */, at(2, 3) /* existing column, above (2,5) */, at(2, 4)),
                tilesAt(at(1, 5), at(2, 5), at(3, 5)));

        // Row (0,5)-(3,5): 4 points. Column (2,3)-(2,5): 3 points. Total: 7.
        assertScore("Multiple new tiles simultaneously extend a row and a column", 7, board);
    }

    @Test
    void testCompletingSixTileLineScoresQwirkleBonus() {
        Board board = new Board(
                tilesAt(at(2, 1), at(2, 2), at(2, 3), at(2, 4), at(2, 5)),
                tilesAt(at(2, 6)));

        // Column (2,1)-(2,6): 6 tiles -> 6 + 6 bonus = 12 points.
        assertScore("A completed six-tile line earns the Qwirkle bonus", 12, board);
    }

    @Test
    void testCrossingPendingTileCanCompleteAQwirkleOnOneAxis() {
        Board board = new Board(
                tilesAt(at(0, 5), at(1, 5), at(2, 0), at(2, 1), at(2, 2), at(2, 3), at(2, 4)),
                tilesAt(at(2, 5)));

        // Horizontal row (0,5)-(2,5): 3 points. Vertical column (2,0)-(2,5): 6 tiles -> 6 + 6 bonus = 12. Total: 15.
        assertScore("A crossing tile simultaneously completes a Qwirkle on one axis", 15, board);
    }

    private static void assertScore(String testName, int expectedScore, Board board) {
        int actualScore = ScoreCalculator.calculatePendingTilesScore(board.getPlacedTiles(), board.getPendingTiles());
        assertEquals(expectedScore, actualScore, testName);
    }

    private static Map<Position, Tile> tilesAt(Position... positions) {
        Map<Position, Tile> tiles = new HashMap<>();
        for (Position position : positions) {
            tiles.put(position, tile());
        }
        return tiles;
    }

    private static Position at(int x, int y) {
        return new Position(x, y);
    }

    private static Tile tile() {
        return new Tile(TileColor.RED, TileSymbol.CIRCLE);
    }
}

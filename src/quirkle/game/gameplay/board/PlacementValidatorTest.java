package quirkle.game.gameplay.board;

import quirkle.game.gameplay.tiles.Tile;
import quirkle.game.gameplay.tiles.TileColor;
import quirkle.game.gameplay.tiles.TileSymbol;
import quirkle.testing.Test;

import java.util.HashMap;
import java.util.Map;

import static quirkle.testing.Assertions.assertEquals;

public class PlacementValidatorTest {

    @Test
    void testPendingTilesFormCommonRow() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.GREEN, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(
                at(1, 0, TileColor.GREEN, TileSymbol.HEXAGON),
                at(2, 0, TileColor.GREEN, TileSymbol.CROSS));

        assertPlacementPossible("Neue Steine bilden eine gemeinsame Zeile", true, placed, pending);
    }

    @Test
    void testPendingTilesFormCommonColumn() {
        Map<Position, Tile> placed = tilesOf(at(3, 3, TileColor.BLUE, TileSymbol.HEXAGON));
        Map<Position, Tile> pending = tilesOf(
                at(3, 4, TileColor.BLUE, TileSymbol.CROSS),
                at(3, 5, TileColor.BLUE, TileSymbol.STAR));

        assertPlacementPossible("Neue Steine bilden eine gemeinsame Spalte", true, placed, pending);
    }

    @Test
    void testPendingTilesNotAligned() {
        Map<Position, Tile> placed = tilesOf();
        Map<Position, Tile> pending = tilesOf(
                at(0, 0, TileColor.RED, TileSymbol.CIRCLE),
                at(1, 1, TileColor.RED, TileSymbol.SQUARE));

        assertPlacementPossible("Diagonal liegende Steine ohne gemeinsame Zeile/Spalte sind ungültig", false, placed, pending);
    }

    @Test
    void testFirstMoveNeedsNoNeighbors() {
        Map<Position, Tile> placed = tilesOf();
        Map<Position, Tile> pending = tilesOf(
                at(0, 0, TileColor.RED, TileSymbol.CIRCLE),
                at(1, 0, TileColor.RED, TileSymbol.SQUARE));

        assertPlacementPossible("Der erste Zug auf einem leeren Brett braucht keine Nachbarn", true, placed, pending);
    }

    @Test
    void testPendingTileAdjacentToExistingTile() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(at(1, 0, TileColor.RED, TileSymbol.SQUARE));

        assertPlacementPossible("Ein neuer Stein, der an einen vorhandenen Stein angrenzt, ist gültig", true, placed, pending);
    }

    @Test
    void testPendingTilesNotAdjacentToAnyExistingTile() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(
                at(5, 5, TileColor.RED, TileSymbol.SQUARE),
                at(6, 5, TileColor.RED, TileSymbol.DIAMOND));

        assertPlacementPossible("Neue Steine, die keinen vorhandenen Stein berühren, sind ungültig", false, placed, pending);
    }

    @Test
    void testRowWithConstantColorAndDistinctSymbols() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(
                at(1, 0, TileColor.RED, TileSymbol.SQUARE),
                at(2, 0, TileColor.RED, TileSymbol.DIAMOND));

        assertPlacementPossible("Gleiche Farbe mit unterschiedlichen Symbolen ist eine gültige Reihe", true, placed, pending);
    }

    @Test
    void testRowWithConstantSymbolAndDistinctColors() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(
                at(1, 0, TileColor.BLUE, TileSymbol.CIRCLE),
                at(2, 0, TileColor.GREEN, TileSymbol.CIRCLE));

        assertPlacementPossible("Gleiches Symbol mit unterschiedlichen Farben ist eine gültige Reihe", true, placed, pending);
    }

    @Test
    void testRowWithDuplicateSymbol() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(at(1, 0, TileColor.RED, TileSymbol.CIRCLE));

        assertPlacementPossible("Ein doppelt vorkommendes Symbol in der Reihe ist ungültig", false, placed, pending);
    }

    @Test
    void testRowWithDuplicateSymbolAtADistance() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(
                at(1, 0, TileColor.RED, TileSymbol.SQUARE),
                at(2, 0, TileColor.RED, TileSymbol.CIRCLE));

        assertPlacementPossible("Ein doppelt vorkommendes Symbol in der Reihe ist auch bei nicht direkt benachbarten Steinen ungültig", false, placed, pending);
    }

    @Test
    void testRowWithDuplicateColor() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(
                at(1, 0, TileColor.BLUE, TileSymbol.CIRCLE),
                at(2, 0, TileColor.RED, TileSymbol.CIRCLE));

        assertPlacementPossible("Eine doppelt vorkommende Farbe in der Reihe ist ungültig", false, placed, pending);
    }

    @Test
    void testRowMixingColorAndSymbolInconsistently() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(
                at(1, 0, TileColor.RED, TileSymbol.SQUARE),
                at(2, 0, TileColor.BLUE, TileSymbol.SQUARE));

        assertPlacementPossible("Eine Reihe muss durchgehend die gleiche Farbe ODER das gleiche Symbol haben", false, placed, pending);
    }

    @Test
    void testRowOfExactlySixTilesIsValid() {
        Map<Position, Tile> placed = tilesOf(
                at(0, 0, TileColor.RED, TileSymbol.CIRCLE),
                at(1, 0, TileColor.RED, TileSymbol.SQUARE),
                at(2, 0, TileColor.RED, TileSymbol.DIAMOND),
                at(3, 0, TileColor.RED, TileSymbol.HEXAGON),
                at(4, 0, TileColor.RED, TileSymbol.STAR));
        Map<Position, Tile> pending = tilesOf(at(5, 0, TileColor.RED, TileSymbol.CROSS));

        assertPlacementPossible("Eine vollständige Reihe aus genau sechs Steinen ist gültig", true, placed, pending);
    }

    @Test
    void testRowOfSevenTilesIsInvalid() {
        Map<Position, Tile> placed = tilesOf(
                at(0, 0, TileColor.RED, TileSymbol.CIRCLE),
                at(1, 0, TileColor.RED, TileSymbol.SQUARE),
                at(2, 0, TileColor.RED, TileSymbol.DIAMOND),
                at(3, 0, TileColor.RED, TileSymbol.HEXAGON),
                at(4, 0, TileColor.RED, TileSymbol.STAR));
        Map<Position, Tile> pending = tilesOf(
                at(5, 0, TileColor.RED, TileSymbol.CROSS),
                at(6, 0, TileColor.RED, TileSymbol.CIRCLE));

        assertPlacementPossible("Eine Reihe mit mehr als sechs Steinen ist ungültig", false, placed, pending);
    }

    @Test
    void testPendingTilesWithGapInRow() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(
                at(1, 0, TileColor.RED, TileSymbol.SQUARE),
                at(3, 0, TileColor.RED, TileSymbol.DIAMOND));

        assertPlacementPossible("Neue Steine mit einer Lücke dazwischen sind ungültig", false, placed, pending);
    }

    @Test
    void testPendingTilesWithGapInColumn() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(
                at(0, 1, TileColor.RED, TileSymbol.SQUARE),
                at(0, 3, TileColor.RED, TileSymbol.DIAMOND));

        assertPlacementPossible("Neue Steine mit einer Lücke in der Spalte sind ungültig", false, placed, pending);
    }

    @Test
    void testPendingTilesAroundAnExistingTile() {
        Map<Position, Tile> placed = tilesOf(at(1, 0, TileColor.RED, TileSymbol.SQUARE));
        Map<Position, Tile> pending = tilesOf(
                at(0, 0, TileColor.RED, TileSymbol.CIRCLE),
                at(2, 0, TileColor.RED, TileSymbol.DIAMOND));

        assertPlacementPossible("Ein bereits liegender Stein trennt die beiden Steine", false, placed, pending);
    }

    private static void assertPlacementPossible(String testName, boolean expected, Map<Position, Tile> placedTiles, Map<Position, Tile> pendingTiles) {
        boolean actual = PlacementValidator.isPendingTilePlacementPossible(placedTiles, pendingTiles);
        assertEquals(expected, actual, testName);
    }

    private record Placement(Position position, Tile tile) {}

    private static Placement at(int x, int y, TileColor color, TileSymbol symbol) {
        return new Placement(new Position(x, y), new Tile(color, symbol));
    }

    private static Map<Position, Tile> tilesOf(Placement... placements) {
        Map<Position, Tile> tiles = new HashMap<>();
        for (Placement placement : placements) {
            tiles.put(placement.position(), placement.tile());
        }
        return tiles;
    }
}

package quirkle.game.gameplay;

import quirkle.game.gameplay.board.Board;
import quirkle.game.gameplay.board.Position;
import quirkle.game.gameplay.player.Player;
import quirkle.game.gameplay.tiles.Tile;
import quirkle.game.gameplay.tiles.TileBag;
import quirkle.testing.Test;

import java.util.List;
import java.util.Random;

import static quirkle.testing.Assertions.assertEquals;
import static quirkle.testing.Assertions.assertFalse;
import static quirkle.testing.Assertions.assertThrows;
import static quirkle.testing.Assertions.assertTrue;

public class GameTest {

    private Game newGame() {
        return new Game(
                new Board(),
                new TileBag(new Random(42)),
                List.of(new Player("A"), new Player("B")));
    }

    @Test
    public void dealsAFullHandToEveryPlayer() {
        Game game = newGame();

        for (Player player : game.getPlayers()) {
            assertEquals(Player.HAND_SIZE, player.getHand().size(), "every player starts with a full hand");
        }
    }

    @Test
    public void placingATileTakesItOffTheRack() {
        Game game = newGame();
        Tile tile = game.getCurrentPlayer().getHand().getFirst();

        game.placeTile(new Position(0, 0), tile);

        assertFalse(game.getCurrentPlayer().hasTile(tile), "placed tile leaves the rack");
        assertEquals(Player.HAND_SIZE - 1, game.getCurrentPlayer().getHand().size(), "hand shrinks by one");
        assertTrue(game.hasPendingTiles(), "the tile is staged, not yet committed");
    }

    @Test
    public void cannotPlaceATileTheCurrentPlayerDoesNotHold() {
        Game game = newGame();
        Tile foreignTile = game.getPlayers().get(1).getHand().getFirst();

        assertThrows(IllegalStateException.class,
                () -> game.placeTile(new Position(0, 0), foreignTile),
                "placing another player's tile is rejected");
    }

    @Test
    public void anIllegalPlacementLeavesTheRackUntouched() {
        Game game = newGame();
        Player player = game.getCurrentPlayer();
        Tile tile = player.getHand().getFirst();

        game.placeTile(new Position(0, 0), tile);

        Tile secondTile = player.getHand().getFirst();
        assertThrows(IllegalStateException.class,
                () -> game.placeTile(new Position(0, 0), secondTile),
                "the occupied position is rejected");
        assertTrue(player.hasTile(secondTile), "the rejected tile stays on the rack");
    }

    @Test
    public void endTurnScoresRefillsAndPassesOn() {
        Game game = newGame();
        Player firstPlayer = game.getCurrentPlayer();
        Tile tile = firstPlayer.getHand().getFirst();

        game.placeTile(new Position(0, 0), tile);
        int points = game.endTurn();

        assertEquals(1, points, "a single opening tile scores one point");
        assertEquals(1, firstPlayer.getScore(), "the points are credited to the player who played them");
        assertEquals(Player.HAND_SIZE, firstPlayer.getHand().size(), "the hand is refilled");
        assertEquals(1, game.getCurrentPlayerIndex(), "the turn passes to the next player");
        assertFalse(game.hasPendingTiles(), "the pending tiles are committed");
    }

    @Test
    public void cannotEndATurnWithoutPlacingATile() {
        Game game = newGame();

        assertThrows(IllegalStateException.class, game::endTurn, "an empty turn cannot be ended");
    }

    @Test
    public void turnOrderWrapsAroundToTheFirstPlayer() {
        Game soloGame = new Game(new Board(), new TileBag(new Random(42)), List.of(new Player("A")));

        soloGame.placeTile(new Position(0, 0), soloGame.getCurrentPlayer().getHand().getFirst());
        soloGame.endTurn();

        assertEquals(0, soloGame.getCurrentPlayerIndex(), "after the last player it is the first player's turn again");
    }
}
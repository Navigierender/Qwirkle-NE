package quirkle.game.gameplay;

import quirkle.game.gameplay.board.Board;
import quirkle.game.gameplay.board.Position;
import quirkle.game.gameplay.player.Player;
import quirkle.game.gameplay.tiles.Tile;
import quirkle.game.gameplay.tiles.TileBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Game {
    private final Board board;
    private final TileBag tileBag;
    private final List<Player> players;
    private int currentPlayerIndex;

    /**
     * The turn being played right now, counted from one across all players.
     *
     * @note Counts turns, not rounds
     */
    private int turnNumber = 1;

    public Game(Board board, TileBag tileBag, List<Player> players) {
        if (players.isEmpty()) {
            throw new IllegalArgumentException("Cannot start a game without players.");
        }

        this.board = board;
        this.tileBag = tileBag;
        this.players = new ArrayList<>(players);
        this.currentPlayerIndex = 0;

        dealStartHands();
    }

    public Board getBoard() {
        return board;
    }

    public TileBag getTileBag() {
        return tileBag;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    /**
     * @return the players in the order they will play from here on
     */
    public List<Player> getTurnOrder() {
        List<Player> turnOrder = new ArrayList<>(players.size());
        for (int i = 0; i < players.size(); i++) {
            turnOrder.add(players.get((currentPlayerIndex + i) % players.size()));
        }
        return turnOrder;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    /** @return true if the bag is empty and a player has no tiles left */
    public boolean isOver() {
        if (tileBag.getSize() > 0) return false;

        return players.stream().anyMatch(player -> player.getHand().isEmpty());
    }

    /**
     * @return the player with the highest score
     * @note on a draw the player who comes first in the list wins
     */
    public Player getWinner() {
        return players.stream().max(Comparator.comparingInt(Player::getScore)).orElseThrow();
    }

    public boolean hasPendingTiles() {
        return !board.getPendingTiles().isEmpty();
    }

    public void placeTile(Position position, Tile tile) {
        Player currentPlayer = getCurrentPlayer();
        if (!currentPlayer.hasTile(tile)) {
            throw new IllegalStateException("Cannot place tile; it is not on the current player's rack.");
        }

        board.placeTile(position, tile);
        currentPlayer.removeTile(tile);
    }

    /**
     * Takes the tile staged at {@code position} back into the current player's hand.
     *
     * @note If the remaining tiles form an invalid move all tiles go back with it
     * @throws IllegalStateException if no tile was staged at {@code position} this turn
     */
    public void takeBackTile(Position position) {
        Tile tile = board.removePendingTile(position);
        if (tile == null) {
            throw new IllegalStateException("Cannot take back tile; no tile was placed there this turn.");
        }

        Player currentPlayer = getCurrentPlayer();
        currentPlayer.addTile(tile);

        if (board.isPendingPlacementLegal()) return;

        takeBackAllTiles();
    }

    /**
     * Takes every tile staged this turn back into the current player's hand.
     */
    public void takeBackAllTiles() {
        Player currentPlayer = getCurrentPlayer();

        for (Tile staged : board.removeAllPendingTiles()) {
            currentPlayer.addTile(staged);
        }
    }

    /**
     * commits the pending tiles, credits the points, refills the hand and passes the turn on
     *
     * @return the points scored this turn
     * @throws IllegalStateException if no tile was placed this turn
     */
    public int endTurn() {
        if (!hasPendingTiles()) {
            throw new IllegalStateException("Cannot end turn, no tile was placed.");
        }

        Player currentPlayer = getCurrentPlayer();
        int points = board.commitPendingTiles();

        currentPlayer.increaseScore(points);
        currentPlayer.setLastRoundScore(points);
        currentPlayer.refillHand(tileBag);

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        turnNumber++;

        return points;
    }

    /**
     * Makes the player at {@code index} the one to move.
     *
     * @throws IndexOutOfBoundsException if the game has no player at {@code index}
     */
    public void setCurrentPlayerIndex(int index) {
        if (index < 0 || index >= players.size()) {
            throw new IndexOutOfBoundsException("No player at index " + index + "; the game has " + players.size() + ".");
        }

        if (index == currentPlayerIndex) return;

        takeBackAllTiles();
        currentPlayerIndex = index;
    }

    /** Deals a full hand to every player, in turn order.*/
    private void dealStartHands() {
        for (Player player : players) {
            player.refillHand(tileBag);
        }
    }
}

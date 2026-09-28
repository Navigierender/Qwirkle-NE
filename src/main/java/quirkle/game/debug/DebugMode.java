package quirkle.game.debug;

import quirkle.engine.EngineConfig;
import quirkle.game.gameplay.Game;
import quirkle.game.gameplay.board.Board;
import quirkle.game.gameplay.board.PlacementResult;
import quirkle.game.gameplay.board.Position;
import quirkle.game.gameplay.player.Player;
import quirkle.game.gameplay.tiles.Tile;
import quirkle.game.gameplay.tiles.TileBag;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * All debug actions. Each one reads its input from the {@link DebugConsole} and changes the attached {@link Game}.
 * The game is stored statically so the buttons can reach it (see {@link #attach} and {@link #detach}).
 * @note the actions ignore the game rules on purpose
 */
public final class DebugMode {

    private static Game game;

    private DebugMode() {}

    /** sets the game the debug actions work on */
    public static void attach(Game currentGame) {
        game = currentGame;
        EngineConfig.message("attached to a game", DebugMode.class.getSimpleName(), EngineConfig.messageType.INFO);
    }

    /** removes the game again, actions abort until the next attach */
    public static void detach() {
        game = null;
        EngineConfig.message("detached", DebugMode.class.getSimpleName(), EngineConfig.messageType.INFO);
    }

    /** @return true if a game is attached */
    public static boolean hasGame() {
        return game != null;
    }

    /**
     * Places multiple tiles on the board, entered one by one.
     * The board is printed after every tile. At the end the whole move is checked and only placed if it is legal.
     */
    public static void placeTiles() {
        run("PLACE TILES", () -> {
            Board board = requireGame().getBoard();

            int count = DebugConsole.readInt("  how many tiles", 1, Player.HAND_SIZE);

            Map<Position, Tile> move = new LinkedHashMap<>();
            for (int i = 0; i < count; i++) {
                println("tile " + (i + 1) + " of " + count + ":");
                println(DebugPrompts.formatBoard(new Board(board.getPlacedTiles(), move)));

                Position position = DebugPrompts.readPosition("  cell");
                Tile tile = DebugPrompts.readTile("  tile for " + describe(position));

                if (move.put(position, tile) != null) {
                    println("  replaced the tile entered for " + describe(position) + " earlier");
                    i--;
                }
            }

            Board testBoard = new Board(board.getPlacedTiles(), move);
            PlacementResult result = testBoard.checkPendingPlacement();

            DebugConsole.printSeparator();
            if (!result.isLegal()) {
                println("illegal move: " + result.getDescription());
                return;
            }
            println("legal move   -   " + testBoard.getPendingScore() + " points");

            board.placeTiles(move);
        });
    }

    /**
     * Puts chosen tiles on a players rack.
     * @note if the rack is not empty the tile in the chosen slot is replaced (it does not go back into the bag)
     */
    public static void setHandTile() {
        run("SET HAND TILE", () -> {
            Game current = requireGame();

            Player player = current.getPlayers().get(DebugPrompts.readPlayerIndex(current, "rack"));

            if (player.getHand().isEmpty()) {
                int count = DebugConsole.readInt("  how many tiles", 1, Player.HAND_SIZE);

                for (int i = 0; i < count; i++) {
                    println("tile " + (i + 1) + " of " + count + ":");
                    println(player.getName() + ": " + DebugPrompts.formatHand(player.getHand()));

                    Tile tile = DebugPrompts.readTile("new tile for the empty rack");
                    player.addTile(tile);
                    println("added " + DebugPrompts.format(tile));
                }
                return;
            }

            println(player.getName() + ": " + DebugPrompts.formatHand(player.getHand()));
            int count = DebugConsole.readInt("  how many slots to change", 1, player.getHand().size());

            for (int i = 0; i < count; i++) {
                println("slot " + (i + 1) + " of " + count + ":");
                println(player.getName() + ": " + DebugPrompts.formatHand(player.getHand()));

                int slot = DebugConsole.readInt("  slot", 0, player.getHand().size() - 1);
                Tile tile = DebugPrompts.readTile("tile for slot " + slot);

                Tile replaced = player.replaceTile(slot, tile);
                println("slot " + slot + ": " + DebugPrompts.format(replaced) + " -> " + DebugPrompts.format(tile));
            }
        });
    }

    /**
     * Puts a chosen tile on top of the bag so it gets drawn next.
     * Together with {@link #clearBag()} you can build the whole draw pile yourself.
     */
    public static void stackTileOnBag() {
        run("STACK TILE ON BAG", () -> {
            TileBag bag = requireGame().getTileBag();

            Tile tile = DebugPrompts.readTile("next tile for the bag");
            bag.putOnTop(tile);

            println("next draw: " + DebugPrompts.format(tile) + "   (" + bag.getSize() + " tiles in the bag)");
        });
    }

    /** Empties the bag after asking for confirmation. Useful to test the end of the game. */
    public static void clearBag() {
        run("CLEAR BAG", () -> {
            TileBag bag = requireGame().getTileBag();

            if (!DebugConsole.readYesNo("throw away the " + bag.getSize() + " tiles left in the bag?")) {
                println("kept the bag as it is");
                return;
            }

            bag.clear();
            println("bag emptied");
        });
    }

    /** Prints score, last turn points and hand size of every player */
    public static void printScores() {
        run("SCORES", () -> {
            Game current = requireGame();

            println("Turn " + current.getTurnNumber() + "   -   tiles left in bag: " + current.getTileBag().getSize());
            DebugConsole.printSeparator();
            println(String.format("%-2s %-14s %7s %11s %6s", "", "PLAYER", "SCORE", "LAST TURN", "HAND"));

            List<Player> players = current.getPlayers();
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                String marker = i == current.getCurrentPlayerIndex() ? ">" : " ";

                println(String.format("%-2s %-14s %7d %11d %6d",
                        marker,
                        player.getName(),
                        player.getScore(),
                        player.getLastRoundScore(),
                        player.getHand().size()));
            }
        });
    }

    /** Sets the player whose turn it is. Score and turn number stay the same. */
    public static void setCurrentPlayer() {
        run("SET STARTING PLAYER", () -> {
            Game current = requireGame();

            current.setCurrentPlayerIndex(DebugPrompts.readPlayerIndex(current, "player to move"));
            println(current.getCurrentPlayer().getName() + " is to move");
        });
    }

    /** @return {@code position} as {@code "(x, y)"}. */
    private static String describe(Position position) {
        return "(" + position.x() + ", " + position.y() + ")";
    }

    /**
     * @return the attached game
     * @throws IllegalStateException if no game is attached
     */
    private static Game requireGame() {
        if (game == null) {
            throw new IllegalStateException("no game running; start a game first");
        }
        return game;
    }

    /** Runs an action and prints errors instead of crashing the game */
    private static void run(String title, Runnable action) {
        DebugConsole.printHeading(title);
        try {
            action.run();
        } catch (RuntimeException e) {
            println("aborted: " + e.getMessage());
        }
        DebugConsole.printBlank();
    }

    private static void println(String line) {
        DebugConsole.println(line);
    }
}

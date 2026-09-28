package quirkle.game.debug;

import quirkle.game.gameplay.Game;
import quirkle.game.gameplay.board.Board;
import quirkle.game.gameplay.board.Position;
import quirkle.game.gameplay.player.Player;
import quirkle.game.gameplay.tiles.Tile;
import quirkle.game.gameplay.tiles.TileColor;
import quirkle.game.gameplay.tiles.TileSymbol;

import java.util.List;

/** Reads and prints tiles, positions and players on the debug console */
public final class DebugPrompts {

    /** shown for an empty cell */
    private static final String NO_TILE = "-";

    private DebugPrompts() {}

    /**
     * Asks for a tile as two letters, e.g. "RQ" for a red square.
     * @note creates a new tile, so it does not have to be in the bag or on a rack
     */
    public static Tile readTile(String what) {
        DebugConsole.println(what + ":");
        DebugConsole.println("  colors: " + colorLegend());
        DebugConsole.println("  symbols: " + symbolLegend());

        while (true) {
            String input = DebugConsole.readLine("  tile (e.g. RQ)").toUpperCase();

            if (input.length() == 2) {
                TileColor color = colorFromLetter(input.charAt(0));
                TileSymbol symbol = symbolFromLetter(input.charAt(1));
                if (color != null && symbol != null) return new Tile(color, symbol);
            }

            DebugConsole.println("  '" + input + "' is not a color+symbol code, e.g. RQ.");
        }
    }

    /** width of one cell in formatBoard */
    private static final int BOARD_CELL_WIDTH = 4;

    /**
     * @return the board as a text grid, x coordinates on top and y on the left
     * @note one empty row/column is added around the tiles so the neighbors can be entered too
     */
    public static String formatBoard(Board board) {
        int minX = board.minX() - 1;
        int maxX = board.maxX() + 1;
        int minY = board.minY() - 1;
        int maxY = board.maxY() + 1;

        StringBuilder out = new StringBuilder(boardCell(""));
        for (int x = minX; x <= maxX; x++) {
            out.append(boardCell(String.valueOf(x)));
        }

        for (int y = minY; y <= maxY; y++) {
            out.append(System.lineSeparator()).append(boardCell(String.valueOf(y)));
            for (int x = minX; x <= maxX; x++) {
                out.append(boardCell(shortFormat(board.getTileAt(new Position(x, y)))));
            }
        }

        return out.toString();
    }

    /** pads text to BOARD_CELL_WIDTH */
    private static String boardCell(String text) {
        return String.format("%" + BOARD_CELL_WIDTH + "s", text);
    }

    /** Asks for the x and y coordinate of a cell */
    public static Position readPosition(String what) {
        DebugConsole.println(what + ":");
        int x = DebugConsole.readInt("  x");
        int y = DebugConsole.readInt("  y");
        return new Position(x, y);
    }

    /**
     * Lists all players and asks for one.
     * @return index of the chosen player
     */
    public static int readPlayerIndex(Game game, String what) {
        List<Player> players = game.getPlayers();

        DebugConsole.println(what + ":");
        for (int i = 0; i < players.size(); i++) {
            String marker = i == game.getCurrentPlayerIndex() ? ">" : " ";
            DebugConsole.println("  " + marker + " [" + i + "] " + players.get(i).getName());
        }
        return DebugConsole.readInt("  choice", 0, players.size() - 1);
    }

    /** @return e.g. "RED CIRCLE", or "-" for null */
    public static String format(Tile tile) {
        if (tile == null) return NO_TILE;
        return tile.getColor().name() + " " + tile.getSymbol().name();
    }

    /**
     * @return the tile as two letters, e.g. "RC" for a red circle, or "-" for null
     * @note symbols have their own letters because SQUARE and STAR start with the same one
     */
    public static String shortFormat(Tile tile) {
        if (tile == null) return NO_TILE;
        return "" + tile.getColor().name().charAt(0) + symbolLetter(tile.getSymbol());
    }

    /** @return the hand in one line with slot numbers */
    public static String formatHand(List<Tile> hand) {
        if (hand.isEmpty()) return "(empty)";

        StringBuilder line = new StringBuilder();
        for (int slot = 0; slot < hand.size(); slot++) {
            if (slot > 0) line.append("  ");
            line.append("[").append(slot).append("] ").append(shortFormat(hand.get(slot)));
        }
        return line.toString();
    }

    /** @return the letter used for a symbol */
    private static char symbolLetter(TileSymbol symbol) {
        return switch (symbol) {
            case CIRCLE -> 'C';
            case SQUARE -> 'Q';
            case DIAMOND -> 'D';
            case HEXAGON -> 'H';
            case STAR -> 'S';
            case CROSS -> 'X';
        };
    }

    /** @return e.g. "R=RED  Y=YELLOW ..." */
    private static String colorLegend() {
        StringBuilder legend = new StringBuilder();
        for (TileColor color : TileColor.values()) {
            if (legend.length() > 0) legend.append("  ");
            legend.append(color.name().charAt(0)).append("=").append(color.name());
        }
        return legend.toString();
    }

    /** @return same as colorLegend but for the symbols */
    private static String symbolLegend() {
        StringBuilder legend = new StringBuilder();
        for (TileSymbol symbol : TileSymbol.values()) {
            if (legend.length() > 0) legend.append("  ");
            legend.append(symbolLetter(symbol)).append("=").append(symbol.name());
        }
        return legend.toString();
    }

    /** @return the color for a letter, or null */
    private static TileColor colorFromLetter(char letter) {
        for (TileColor color : TileColor.values()) {
            if (color.name().charAt(0) == letter) return color;
        }
        return null;
    }

    /** @return the symbol for a letter, or null */
    private static TileSymbol symbolFromLetter(char letter) {
        for (TileSymbol symbol : TileSymbol.values()) {
            if (symbolLetter(symbol) == letter) return symbol;
        }
        return null;
    }
}

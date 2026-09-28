package quirkle.game.gameplay.tiles;

/** A single Qwirkle tile, identified by its color and symbol. */
public class Tile {
    private TileColor color;
    private TileSymbol symbol;

    public Tile(TileColor color, TileSymbol symbol) {
        this.color = color;
        this.symbol = symbol;
    }

    public TileColor getColor() {
        return color;
    }

    public TileSymbol getSymbol() {
        return symbol;
    }
}

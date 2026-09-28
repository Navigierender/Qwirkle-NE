package quirkle.game.gameplay.tiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;

public class TileBag {

    /** Number of tiles created for each color/symbol combination */
    private static final int COPIES_PER_TILE = 3;

    /** Creates a full, shuffled bag using the default source of randomness */
    public TileBag() {
        this(RandomGenerator.getDefault());
    }

    /** Creates a full bag shuffled with {@code random} */
    public TileBag(RandomGenerator random) {
        this.random = random;
        generateStartTiles();
    }

    /** Tiles remaining in the bag, in draw order */
    private final List<Tile> tiles = new ArrayList<>(TileSymbol.values().length * TileColor.values().length * COPIES_PER_TILE);

    private final RandomGenerator random;

    /** Fills {@link #tiles} with a full tile set and shuffles it. */
    private void generateStartTiles() {
        createFullTileSet();
        shuffle();
    }

    /** Adds {@value COPIES_PER_TILE} tiles of every color/symbol combination to {@link #tiles}. */
    private void createFullTileSet() {
        for (TileColor color : TileColor.values()) {
            for (TileSymbol symbol : TileSymbol.values()) {
                for (int i = 0; i < COPIES_PER_TILE; i++) {
                    tiles.add(new Tile(color, symbol));
                }
            }
        }
    }

    /** Shuffles {@link #tiles} using this bag's source of randomness. */
    private void shuffle() {
        Collections.shuffle(tiles, random);
    }

    /**
     * Draws {@code count} tiles from the bag
     *
     * @return the drawn tiles, in draw order
     * @apiNote never throws if the bag holds fewer than {@code count}
     */
    public List<Tile> drawTiles(int count) {
        List<Tile> drawnTiles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            if (!tiles.isEmpty()) {
                drawnTiles.add(tiles.removeFirst());
            }
        }
        return drawnTiles;
    }

    /** Puts {@code tile} on top of the stack, so it is the next one drawn */
    public void putOnTop(Tile tile) {
        tiles.addFirst(tile);
    }

    /** Empties the bag. */
    public void clear() {
        tiles.clear();
    }

    /** @return the number of tiles remaining in the bag. */
    public int getSize() {
        return tiles.size();
    }
}

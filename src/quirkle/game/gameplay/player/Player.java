package quirkle.game.gameplay.player;

import quirkle.engine.AssetManager;
import quirkle.game.gameplay.tiles.Tile;
import quirkle.game.gameplay.tiles.TileBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {
    public static  final int HAND_SIZE = 6;

    private final String name;
    private int score;

    /** Points scored on this player's most recent completed turn */
    private int lastRoundScore;

    private final List<Tile> hand = new ArrayList<>();

    public Player(String name) {
        this.name = name;
        this.score = 0;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void increaseScore(int points) {
        AssetManager.playSound("gameplay/increase_score", 0.08);
        setScore(this.score + points);
    }

    public int getLastRoundScore() {
        return lastRoundScore;
    }

    public void setLastRoundScore(int lastRoundScore) {
        this.lastRoundScore = lastRoundScore;
    }

    public List<Tile> getHand() {
        return Collections.unmodifiableList(hand);
    }

    /**
     * Draws from {@code tileBag} until the hand holds {@value #HAND_SIZE} tiles
     */
    public void refillHand(TileBag tileBag) {
        int cardsToPull = HAND_SIZE - hand.size();
        hand.addAll(tileBag.drawTiles(cardsToPull));
        if (cardsToPull > 0) AssetManager.playSound("gameplay/draw",0.15);
    }

    /** @return if this exact tile instance is on the player's rack */
    public boolean hasTile(Tile tile) {
        return hand.contains(tile);
    }

    /**
     * Puts {@code tile} onto the player's rack.
     *
     * @throws IllegalStateException if the rack already holds {@value #HAND_SIZE} tiles
     */
    public void addTile(Tile tile) {
        if (isHandFull()) {
            throw new IllegalStateException("Cannot add tile; " + name + "'s rack is full.");
        }
        hand.add(tile);
    }

    /**
     * Removes {@code tile} from the player's rack.
     *
     * @throws IllegalStateException if the player does not hold that exact tile
     */
    public void removeTile(Tile tile) {
        if (!hand.remove(tile)) {
            throw new IllegalStateException("Cannot remove tile; " + name + " does not hold it.");
        }
    }

    /** @return if the rack is full, so a tile can only go on it by replacing another. */
    public boolean isHandFull() {
        return hand.size() >= HAND_SIZE;
    }

    /**
     * replaces tile on rack slot {@code slotIndex} with {@code tile}
     *
     * @return the tile that was replaced
     * @throws IndexOutOfBoundsException if the rack has no such slot
     */
    public Tile replaceTile(int slotIndex, Tile tile) {
        if (slotIndex < 0 || slotIndex >= hand.size()) {
            throw new IndexOutOfBoundsException(
                    "No rack slot " + slotIndex + "; " + name + " holds " + hand.size() + " tiles.");
        }
        return hand.set(slotIndex, tile);
    }

    private void setScore(int score) {
        this.score = score;
    }
}

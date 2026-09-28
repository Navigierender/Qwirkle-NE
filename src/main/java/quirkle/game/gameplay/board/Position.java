package quirkle.game.gameplay.board;

/** A coordinate on the game board. */
public record Position(int x, int y) {
    /** @return the neighboring position in the given {@code direction}. */
    public Position neighbor(Direction direction) {
        return new Position(x + direction.dx, y + direction.dy);
    }
}

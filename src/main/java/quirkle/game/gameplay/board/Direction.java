package quirkle.game.gameplay.board;

/** The four directions a tile can neighbor on the board. */
public enum Direction {
    NORTH(0, -1), EAST(1, 0), SOUTH(0, 1), WEST(-1, 0);

    /** Offset added to a position's x/y coordinate for this direction */
    final int dx, dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
}

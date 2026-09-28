package quirkle.game.gameplay.board;

import java.util.function.ToIntFunction;

public enum LineOrientation {
    HORIZONTAL(Position::y, Position::x, Direction.WEST, Direction.EAST),
    VERTICAL(Position::x, Position::y, Direction.NORTH, Direction.SOUTH);

    final ToIntFunction<Position> fixedCoordinate;
    final ToIntFunction<Position> extentCoordinate;
    final Direction backwardDirection;
    final Direction forwardDirection;

    LineOrientation(ToIntFunction<Position> fixedCoordinate, ToIntFunction<Position> extentCoordinate,
                    Direction backwardDirection, Direction forwardDirection) {
        this.fixedCoordinate = fixedCoordinate;
        this.extentCoordinate = extentCoordinate;
        this.backwardDirection = backwardDirection;
        this.forwardDirection = forwardDirection;
    }
}
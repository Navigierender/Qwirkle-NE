package quirkle.game.debug;

import java.util.Arrays;
import java.util.List;

/**
 * All buttons of the debug menu.
 * @note the order here is the order in the menu
 */
public enum DebugAction {

    PLACE_TILES(Group.BOARD, "Place tiles", DebugMode::placeTiles),

    SET_HAND_TILE(Group.HAND, "Set hand tile", DebugMode::setHandTile),

    CLEAR_BAG(Group.BAG, "Clear bag", DebugMode::clearBag),
    STACK_TILE_ON_BAG(Group.BAG, "Stack next tile", DebugMode::stackTileOnBag),

    PRINT_SCORES(Group.TURN, "Print scores", DebugMode::printScores),
    SET_STARTING_PLAYER(Group.TURN, "Set starting player", DebugMode::setCurrentPlayer);

    /** sections of the debug menu */
    public enum Group {
        BOARD("Board"),
        HAND("Hands"),
        BAG("Tile bag"),
        TURN("Turns");

        private final String label;

        Group(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private final Group group;
    private final String label;
    private final Runnable action;

    DebugAction(Group group, String label, Runnable action) {
        this.group = group;
        this.label = label;
        this.action = action;
    }

    public String getLabel() {
        return label;
    }

    /** runs the action, errors are already caught in {@link DebugMode} */
    public void run() {
        action.run();
    }

    /** @return all actions of a group */
    public static List<DebugAction> of(Group group) {
        return Arrays.stream(values()).filter(action -> action.group == group).toList();
    }
}

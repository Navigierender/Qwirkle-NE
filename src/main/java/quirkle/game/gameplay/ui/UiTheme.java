package quirkle.game.gameplay.ui;

import quirkle.engine.AssetManager;
import quirkle.engine.EngineConfig;

import java.awt.Color;
import java.awt.Rectangle;

public final class UiTheme {

    private UiTheme() {}

    // Render layers, applied to Entity#renderOrder

    public static final int LAYER_GRID = -100;
    public static final int LAYER_FRAME = 50;
    public static final int LAYER_HUD = 100;

    // Fonts

    public static final String FONT = "poly_regular";
    public static final String FONT_SCORE = "higher_jump";

    public static final float FONT_SIZE_BUTTON = 24f;
    public static final float FONT_SIZE_BAG_COUNT = 80f;
    public static final float FONT_SIZE_CARD_SCORE = 80f;
    public static final float FONT_SIZE_TURN_CURRENT = 54f;
    public static final float FONT_SIZE_TURN_NEXT = 32f;
    public static final float FONT_SIZE_SCORE_POPUP = 150f;

    // Colors

    public static final Color BACKGROUND = new Color(8, 8, 10);
    public static final Color TEXT = new Color(245, 245, 245);
    public static final Color TEXT_DIMMED = new Color(245, 245, 245, 120);
    public static final Color GOLD = new Color(198, 154, 58);

    /** frame color of the selected tile */
    public static final Color SELECTION = new Color(255, 193, 7);

    /** frame color if placing the tile failed */
    public static final Color REJECTION = new Color(229, 57, 53);

    public static final Color GRID_LINE = new Color(255, 255, 255, 34);
    public static final Color GRID_MARKER = new Color(255, 255, 255, 60);

    public static final Color PLACEHOLDER_FILL = new Color(255, 255, 255, 22);
    public static final Color PLACEHOLDER_BORDER = new Color(255, 255, 255, 60);

    // Frame sprites

    public static final String SPRITE_GOLDEN_FRAME = "gameplay/ui/goldenframe";
    public static final String SPRITE_PLAYER_CARD = "gameplay/ui/player";
    public static final String SPRITE_SLOT_FRAME = "gameplay/ui/tileframe";

    public static final String FRAME_BOARD = "gameplay/ui/frame";

    // Layout: the frame the board is seen through

    private static final double FRAME_SOURCE_WIDTH = 1920.0;
    private static final double FRAME_SOURCE_HEIGHT = 1080.0;

    private static final double FRAME_OPENING_LEFT = 223.0;
    private static final double FRAME_OPENING_TOP = 195.0;
    private static final double FRAME_OPENING_RIGHT = 1686.0;
    private static final double FRAME_OPENING_BOTTOM = 889.0;

    public static Rectangle boardViewport() {
        double scaleX = EngineConfig.WINDOW_WIDTH / FRAME_SOURCE_WIDTH;
        double scaleY = EngineConfig.WINDOW_HEIGHT / FRAME_SOURCE_HEIGHT;

        int left = (int) Math.round(FRAME_OPENING_LEFT * scaleX);
        int top = (int) Math.round(FRAME_OPENING_TOP * scaleY);
        int right = (int) Math.round(FRAME_OPENING_RIGHT * scaleX);
        int bottom = (int) Math.round(FRAME_OPENING_BOTTOM * scaleY);

        return new Rectangle(left, top, right - left, bottom - top);
    }

    // Layout: the turn order along the top edge

    public static final int TURN_TOP = 44;
    public static final int TURN_MARGIN_X = 170;

    /** gap after the name of the current player */
    public static final int TURN_CURRENT_GAP = 52;
    public static final int TURN_NAME_GAP = 30;

    /**
     * duration of the turn order animation
     * @note same as 2 * HANDOVER_PHASE_SECONDS so it matches the rack animation
     */
    public static final double TURN_SHUFFLE_SECONDS = 0.4;

    /** how far the name moves left when it leaves */
    public static final int TURN_LEAVE_SHIFT = 110;

    // Layout: score popup

    public static final double SCORE_POPUP_SECONDS = 1.1;

    /** how far the popup moves up */
    public static final int SCORE_POPUP_RISE = 70;

    // Layout: the board itself
    public static final int BOARD_BASE_CELL_SIZE = 64;

    // Layout: tiles, both on the board and in the rack
    public static final double TILE_PADDING_RATIO = 0.09;

    /** opacity of the placement preview */
    public static final float PLACEMENT_PREVIEW_OPACITY = 0.45f;

    // Layout: the active player's card in the bottom left corner

    public static final int CARD_LEFT = 12;

    /** negative because the sprite has empty space at the bottom */
    public static final int CARD_BOTTOM_MARGIN = -27;

    /** same aspect ratio as player.png */
    public static final int CARD_WIDTH = 329;
    public static final int CARD_HEIGHT = 472;

    public static int cardTop() {
        return EngineConfig.WINDOW_HEIGHT - CARD_BOTTOM_MARGIN - CARD_HEIGHT;
    }

    // Layout: tile bag counter, bottom right

    /** drawn size of the diamond */
    public static final int BAG_SIZE = 340;

    public static final int BAG_MARGIN_X = 175;
    public static final int BAG_MARGIN_Y = 205;

    public static int bagCenterX() {
        return EngineConfig.WINDOW_WIDTH - BAG_MARGIN_X;
    }

    public static int bagCenterY() {
        return EngineConfig.WINDOW_HEIGHT - BAG_MARGIN_Y;
    }

    // Layout: the active player's rack along the bottom

    public static final int RACK_TILE_SIZE = 120;
    public static final int RACK_TILE_SPACING = 165;

    /** size of the frame behind one rack tile */
    public static final int RACK_FRAME_SIZE = 165;

    /** distance from the bottom of the board opening to the middle of the rack */
    private static final int RACK_CENTER_BELOW_OPENING = 59;

    /** @return y center of the rack */
    public static int rackCenterY() {
        Rectangle opening = boardViewport();
        return opening.y + opening.height + RACK_CENTER_BELOW_OPENING;
    }

    public static final double HANDOVER_PHASE_SECONDS = 0.2;

    public static int rackHiddenCenterY() {
        return EngineConfig.WINDOW_HEIGHT + RACK_TILE_SIZE;
    }

    // Layout: side buttons

    public static final int SIDE_BUTTON_WIDTH = 270;
    public static final int SIDE_BUTTON_HEIGHT = 90;

    public static String text(String key) {
        return AssetManager.getMessage(key);
    }
}

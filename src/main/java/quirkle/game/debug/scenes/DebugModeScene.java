package quirkle.game.debug.scenes;

import quirkle.engine.Entity;
import quirkle.engine.Scene;
import quirkle.game.debug.DebugAction;
import quirkle.game.debug.DebugMode;
import quirkle.game.gameplay.ui.SideButton;
import quirkle.game.gameplay.ui.UiTheme;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Debug menu with one button per {@link DebugAction}, sorted by group.
 * Opened from the quick menu.
 */
public class DebugModeScene extends Scene {

    private static final float TITLE_FONT_SIZE = 34f;
    private static final int TOP_MARGIN = 60;
    private static final int TITLE_TO_GROUPS_GAP = 70;
    private static final int HEADING_HEIGHT = 40;
    private static final int BUTTON_GAP = 10;
    private static final int GROUP_GAP = 20;

    private List<SideButton> buttons;
    private SideButton backButton;

    /**
     * @note buttons is created here and not at the field, because Scene calls onCreate() before the field initializers run.
     *       Same reason why the close action is set later with {@link #setOnClose}.
     */
    @Override
    public void onCreate() {
        buttons = new ArrayList<>();

        int centerX = getCenterX();
        int y = TOP_MARGIN;

        addEntities(new SectionHeading("DEBUG MODE", centerX, y, TITLE_FONT_SIZE));
        y += TITLE_TO_GROUPS_GAP;

        for (DebugAction.Group group : DebugAction.Group.values()) {
            addEntities(new SectionHeading(group.getLabel(), centerX, y + HEADING_HEIGHT / 2, UiTheme.FONT_SIZE_BUTTON));
            y += HEADING_HEIGHT;

            for (DebugAction action : DebugAction.of(group)) {
                y += placeActionButton(action, centerX, y);
            }

            y += GROUP_GAP;
        }

        backButton = new SideButton("Back to game", null);
        backButton.place(centerX + UiTheme.SIDE_BUTTON_WIDTH / 2, y + UiTheme.SIDE_BUTTON_HEIGHT / 2);
        addEntities(backButton);
        buttons.add(backButton);
    }

    /** sets what happens when "Back to game" is clicked */
    public void setOnClose(Runnable onClose) {
        backButton.setAction(onClose);
    }

    /**
     * Adds the button of one action. It is disabled if no game is running.
     * @return how far y has to move down for the next button
     */
    private int placeActionButton(DebugAction action, int centerX, int rowTop) {
        SideButton button = new SideButton(action.getLabel(), action::run);
        button.setEnabled(DebugMode.hasGame());
        button.place(centerX + UiTheme.SIDE_BUTTON_WIDTH / 2, rowTop + UiTheme.SIDE_BUTTON_HEIGHT / 2);

        addEntities(button);
        buttons.add(button);

        return UiTheme.SIDE_BUTTON_HEIGHT + BUTTON_GAP;
    }

    @Override
    public void onTick(double dt) {
        for (SideButton button : buttons) {
            button.handleInput();
        }
    }

    /** darkens the scene behind the menu */
    @Override
    public void onRender(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    /** centered text for the title and the group headings */
    private static final class SectionHeading extends Entity {
        private final String text;
        private final float fontSize;

        SectionHeading(String text, int centerX, int centerY, float fontSize) {
            this.text = text;
            this.fontSize = fontSize;
            this.x = centerX;
            this.y = centerY;
        }

        @Override
        public void onRender(Graphics2D g) {
            drawText(text, fontSize, UiTheme.GOLD, UiTheme.FONT, x, y, 0.0, OriginPresets.CENTER, g);
        }
    }
}

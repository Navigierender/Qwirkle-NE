package quirkle.game.gameplay.ui;

import quirkle.game.gameplay.Game;

import java.awt.Graphics2D;

public class PlayerCard extends PanelEntity {

    private final Game game;

    // offset so the number is in the middle of the heart
    private static final int COUNT_OFFSET_X = 20;
    private static final int COUNT_OFFSET_Y = 5;

    public PlayerCard(Game game) {
        this.game = game;
    }

    @Override
    public void onCreate() {
        setSprite(UiTheme.SPRITE_PLAYER_CARD);
        setBounds(UiTheme.CARD_LEFT, UiTheme.cardTop(), UiTheme.CARD_WIDTH, UiTheme.CARD_HEIGHT, OriginPresets.TOP_LEFT);
    }

    @Override
    public void onRender(Graphics2D g) {
        drawText(String.valueOf(game.getCurrentPlayer().getScore()), UiTheme.FONT_SIZE_CARD_SCORE, UiTheme.TEXT,
                UiTheme.FONT_SCORE, getLeft() + (getWidth() / 2) + COUNT_OFFSET_X, getTop() + (getHeight() / 2) + COUNT_OFFSET_Y,
                0.0, OriginPresets.CENTER, g);
    }
}

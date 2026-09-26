package quirkle.game.gameplay.ui;

import quirkle.engine.AssetManager;
import quirkle.game.gameplay.tiles.TileBag;

import java.awt.Graphics2D;

public class TileBagCounter extends PanelEntity {

    private static final int COUNT_OFFSET_Y = 15;

    private final TileBag tileBag;

    public TileBagCounter(TileBag tileBag) {
        this.tileBag = tileBag;
    }

    @Override
    public void onCreate() {
        setBounds(UiTheme.bagCenterX(), UiTheme.bagCenterY(),
                UiTheme.BAG_SIZE, UiTheme.BAG_SIZE, OriginPresets.CENTER);
    }

    @Override
    public void onRender(Graphics2D g) {
        // scale depends on the sprite width, so the size stays right if the asset changes
        double scale = UiTheme.BAG_SIZE / (double) AssetManager.getTexture(UiTheme.SPRITE_GOLDEN_FRAME).getWidth();

        drawSprite(UiTheme.SPRITE_GOLDEN_FRAME, scale, x, y, 0.0, OriginPresets.CENTER, g);

        drawText(String.valueOf(tileBag.getSize()), UiTheme.FONT_SIZE_BAG_COUNT, UiTheme.TEXT,
                UiTheme.FONT_SCORE, x, y + COUNT_OFFSET_Y, 0.0, OriginPresets.CENTER, g);
    }
}

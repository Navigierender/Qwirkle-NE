package quirkle.game.tutorial.entities;

import quirkle.engine.EngineConfig;
import quirkle.engine.Entity;

import java.awt.*;

public class TutorialEntry extends Entity {

    private static final float titleFontSize = 50.0f;
    private static final float descriptionFontSize = 50.0f;

    private static final String titleFontIdentifier = "higher_jump";
    private static final String descriptionFontIdentifier = "poly_regular";

    private static final Color fontColor = Color.WHITE;

    private static final double focusSpriteScaleModifier = 2.0; //this is only for texture compression purposes

    public enum FocusType {
        square_big("tutorial/focus/square_big", -200, 230, -200, 380),
        rectangle_big("tutorial/focus/rectangle_big", -520, 120, -520, 300),
        rectangle_small("tutorial/focus/rectangle_small", -350, 60, -350, 180),
        rectangle_large("tutorial/focus/rectangle_large", -800, 400, -800 , -500);

        public final String spriteIdentifier;

        // unsigned, base magnitudes - sign is applied by the alignment modifiers at use-time
        public final double titleDx;
        public final double titleDy;
        public final double descriptionDx;
        public final double descriptionDy;

        FocusType(String spriteIdentifier, int titleDx, int titleDy, int descriptionDx, int descriptionDy) {
            this.spriteIdentifier = spriteIdentifier;

            this.titleDx = titleDx;
            this.titleDy = titleDy;
            this.descriptionDx = descriptionDx;
            this.descriptionDy = descriptionDy;
        }
    }

    private String title;
    private String description;
    private FocusType focusType;

    private int horizontalAlignment; // -1 = left, 1 = right
    private int verticalAlignment;   // -1 = up,   1 = down

    private Entity.OriginPresets textOrigin;

    public TutorialEntry(FocusType focusType, int horizontalAlignment, int verticalAlignment, String title, String description, double tarX, double tarY, double oldX, double oldY) {
        this.focusType = focusType;
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;

        this.title = title;
        this.description = description;

        this.targetX = tarX;
        this.targetY = tarY;

        this.x = oldX;
        this.y = oldY;

        if (horizontalAlignment == 1 && verticalAlignment == 1) textOrigin = OriginPresets.TOP_LEFT;
        else if (horizontalAlignment == -1 && verticalAlignment == 1) textOrigin = OriginPresets.TOP_RIGHT;
        else if (horizontalAlignment == 1 && verticalAlignment == -1) textOrigin = OriginPresets.BOTTOM_LEFT;
        else if (horizontalAlignment == -1 && verticalAlignment == -1) textOrigin = OriginPresets.BOTTOM_RIGHT;
        else EngineConfig.message("Unknown text Alignment parsed to TutorialEntry", "TutorialEntry", EngineConfig.messageType.ERROR);
    }

    @Override
    public void onCreate() {
        origin = OriginPresets.CENTER;
        setSprite(focusType.spriteIdentifier);
        scale = focusSpriteScaleModifier;
    }

    @Override
    public void onTick(double dt) {
        moveToTarget(dt);
    }

    @Override
    public void onRender(Graphics2D g) {
        double titleDx = focusType.titleDx * horizontalAlignment;
        double titleDy = focusType.titleDy * verticalAlignment;
        double descriptionDx = focusType.descriptionDx * horizontalAlignment;
        double descriptionDy = focusType.descriptionDy * verticalAlignment;

        drawText(title, titleFontSize, fontColor, titleFontIdentifier,   x + titleDx, y + titleDy, 0.0, textOrigin, g); //title

        drawText(description, descriptionFontSize, fontColor, descriptionFontIdentifier, x + descriptionDx, y + descriptionDy, 0.0, textOrigin, g); //description
    }
}
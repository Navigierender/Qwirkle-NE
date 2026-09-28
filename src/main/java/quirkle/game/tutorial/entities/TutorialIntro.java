package quirkle.game.tutorial.entities;

import quirkle.engine.Entity;
import quirkle.engine.SceneManager;

import java.awt.*;

public class TutorialIntro extends Entity {
    private String title;
    private String content;

    public TutorialIntro(String title, String content) {
        this.title = title;
        this.content = content;
    }

    @Override
    public void onCreate() {
        setSprite("tutorial/introbackground");
        x = SceneManager.getCurrentScene().getCenterX();
        y = SceneManager.getCurrentScene().getCenterY();
        origin = OriginPresets.CENTER;
    }

    @Override
    public void onRender(Graphics2D g) {
        drawText(title, 40.0f, Color.WHITE, "higher_jump", 300, 260, 0.0, OriginPresets.TOP_LEFT, g);
        drawText(content, 40.0f, Color.WHITE, "poly_regular", 300, 400, 0.0, OriginPresets.TOP_LEFT, g);
    }
}

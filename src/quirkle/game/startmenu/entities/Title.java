package quirkle.game.startmenu.entities;

import java.awt.Graphics2D;

import quirkle.engine.*;

public class Title extends Entity {

    String title;

    public Title(String title) {
        this.title = title;
        setSprite("startmenu/title");
        scale = 1.6/2;
        origin = OriginPresets.CENTER;
        x = SceneManager.getCurrentScene().getCenterX();
        y = (int) (SceneManager.getCurrentScene().getHeight() / 2 / 1.8);
    }

    @Override
    public void onRender(Graphics2D g) {
        //drawText(AssetManager.getMessage("title"), 110, Color.white, "higher_jump", x, y+10, 0.0, OriginPresets.CENTER, g);
    }
}

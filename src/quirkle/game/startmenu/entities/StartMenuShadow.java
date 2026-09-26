package quirkle.game.startmenu.entities;

import quirkle.engine.Entity;
import quirkle.engine.SceneManager;

public class StartMenuShadow extends Entity {

    public void onCreate() {
        setSprite("startmenu/shadow");
        origin = OriginPresets.CENTER;
        x = SceneManager.getCurrentScene().getCenterX();
    }
}

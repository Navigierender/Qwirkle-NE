package quirkle.game.startmenu.entities;

import org.navigierender.rslair.Entity;
import org.navigierender.rslair.SceneManager;

public class StartMenuShadow extends Entity {

    public void onCreate() {
        setSprite("startmenu/shadow");
        origin = OriginPresets.CENTER;
        x = SceneManager.getCurrentScene().getCenterX();
    }
}

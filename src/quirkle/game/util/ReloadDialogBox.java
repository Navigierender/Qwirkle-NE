package quirkle.game.util;

import quirkle.engine.AssetManager;
import quirkle.engine.Entity;
import quirkle.engine.Scene;
import quirkle.engine.SceneManager;
import quirkle.game.startmenu.scenes.StartMenuScene;

import java.awt.*;

public class ReloadDialogBox extends Scene {
    public static String fontIdentifier = "poly_regular";

    public String message;
    public RectangularButton continueButton;
    public RectangularButton cancelButton;

    public int buttonSpacing = 40;

    public ReloadDialogBox(String message) {
        this.message = message;
    }

    @Override
    public void onCreate() {
        SimpleSpriteEntity background = new SimpleSpriteEntity("util/vignette", Entity.OriginPresets.CENTER);
        SimpleSpriteEntity dialogBox = new SimpleSpriteEntity("util/reloaddialogbox/background", Entity.OriginPresets.CENTER);
        SimpleTextEntity dialog = new SimpleTextEntity(message, 40, "poly_regular", Color.WHITE, Entity.OriginPresets.CENTER);

        continueButton = new RectangularButton(AssetManager.getMessage("proceed"));
        cancelButton = new RectangularButton(AssetManager.getMessage("cancel"));

        addEntities(background, dialogBox, dialog, continueButton, cancelButton);

        for (Entity e : sceneEntities) {
            e.x = SceneManager.getCurrentScene().getCenterX();
            e.y = SceneManager.getCurrentScene().getCenterY();
        }

        continueButton.y += dialogBox.getScaledHeight() * 0.7; cancelButton.y += dialogBox.getScaledHeight() * 0.7;

        continueButton.x += continueButton.getScaledWidth() * 0.5 + buttonSpacing / 2;
        cancelButton.x -= cancelButton.getScaledWidth() * 0.5 + buttonSpacing / 2;
    }

    @Override
    public void onTick(double dt) {
        if (continueButton.isClicked()) SceneManager.setScene(new StartMenuScene());
        if (cancelButton.isClicked()) SceneManager.setScene(new StartMenuScene());
    }
}
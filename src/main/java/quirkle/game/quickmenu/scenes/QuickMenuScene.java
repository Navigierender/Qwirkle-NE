package quirkle.game.quickmenu.scenes;

import quirkle.PersistentData;
import quirkle.engine.*;

import quirkle.game.credits.scenes.CreditsScene;
import quirkle.game.debug.DebugIndicator;
import quirkle.game.debug.scenes.DebugModeScene;
import quirkle.game.settings.scenes.SettingsScene;
import quirkle.game.startmenu.scenes.StartMenuScene;
import quirkle.game.tutorial.scenes.TutorialScene;
import quirkle.game.util.*;

import java.awt.*;
import java.awt.event.KeyEvent;

public class QuickMenuScene extends Scene {
    private RectangularButton buttonContinue;
    private RectangularButton buttonSettings;
    private RectangularButton buttonTutorial;
    private RectangularButton buttonCredits;
    private RectangularButton buttonBack;

    /** Opens the debug panel; only built in debug mode, so it is {@code null} the rest of the time */
    private DebugIndicator debugIndicator;

    /** The debug panel while it is open, {@code null} otherwise */
    private DebugModeScene debugModeScene;

    private static int buttonSpacing = 20;
    private static float optionsTitleFontSize = 80;

    @Override
    public void onCreate() {
        SimpleSpriteEntity background = new SimpleSpriteEntity("quickmenu/background", Entity.OriginPresets.TOP_LEFT);
        SimpleTextEntity optionsTitle = new SimpleTextEntity(AssetManager.getMessage("options"), optionsTitleFontSize, "higher_jump", Color.WHITE, Entity.OriginPresets.BOTTOM_LEFT);

        buttonBack = new RectangularButton(AssetManager.getMessage("mainmenu"));
        buttonContinue = new RectangularButton(AssetManager.getMessage("return"));
        buttonSettings = new RectangularButton(AssetManager.getMessage("settings"));
        buttonTutorial = new RectangularButton(AssetManager.getMessage("tutorial"));
        buttonCredits = new RectangularButton(AssetManager.getMessage("credits"));
        buttonContinue = new RectangularButton(AssetManager.getMessage("return"));

        addEntities(background, optionsTitle, buttonBack, buttonSettings, buttonTutorial, buttonCredits, buttonContinue);

        optionsTitle.x = getCenterX()/8;
        optionsTitle.y = getCenterY()*0.8;

        int i = 0;
        for (Entity e : new Entity[] {buttonBack, buttonSettings, buttonTutorial, buttonCredits, buttonContinue}) {
            e.y = getCenterY() + i*buttonContinue.getScaledHeight() + i*buttonSpacing;
            e.x = getCenterX()/3;
            i++;
        }

        if (PersistentData.debugMode) {
            debugIndicator = new DebugIndicator();
            addEntities(debugIndicator);
        }
    }

    private void openDebugMode() {
        if (debugModeScene != null) return;

        debugModeScene = new DebugModeScene();
        debugModeScene.create(); // not routed through SceneManager, so we have to trigger onCreate() ourselves
        debugModeScene.setOnClose(this::closeDebugMode);
    }

    private void closeDebugMode() {
        if (debugModeScene == null) return;

        debugModeScene.destroy();
        debugModeScene = null;
    }

    /** @note The panel goes on last, over the finished frame of the menu below it. */
    @Override
    public void render(Graphics2D g) {
        super.render(g);

        if (debugModeScene != null) debugModeScene.render(g);
    }

    @Override
    public void onTick(double dt) {
        if (debugModeScene != null) {
            // The menu underneath stays put while the panel is open; neither its buttons nor ESC
            // reach through it.
            debugModeScene.update(dt);
            return;
        }

        // The corner indicator is the way in: it already marks debug mode and grows on hover.
        if (debugIndicator != null && debugIndicator.isClicked()) {
            openDebugMode();
            return;
        }

        if (buttonContinue.isClicked()) SceneManager.stopTempScene();
        if (buttonSettings.isClicked()) SceneManager.setTempScene(new SettingsScene(), false, true);
        if (buttonTutorial.isClicked()) SceneManager.setTempScene(new TutorialScene(), false, true);
        if (buttonCredits.isClicked()) SceneManager.setTempScene(new CreditsScene(), false, true);
        if (buttonBack.isClicked()) SceneManager.setScene(new StartMenuScene());

        if (InputManager.isKeyPressed(KeyEvent.VK_ESCAPE)) SceneManager.stopTempScene();
    }
}

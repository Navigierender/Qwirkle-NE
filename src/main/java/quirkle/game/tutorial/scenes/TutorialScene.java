package quirkle.game.tutorial.scenes;

import quirkle.engine.*;
import quirkle.game.tutorial.entities.TutorialEntry;
import quirkle.game.tutorial.entities.TutorialIntro;
import quirkle.game.util.PlayRandomSound;
import quirkle.game.util.SimpleSpriteEntity;

import java.awt.event.KeyEvent;

public class TutorialScene extends Scene {

    private static final int entryCount = 5;
    private static final double entryVelocity = 2000.0;

    @Override
    public void onCreate() {
        SimpleSpriteEntity background = new SimpleSpriteEntity("tutorial/viewport", Entity.OriginPresets.TOP_LEFT);
        addEntities(
                background,
                new TutorialIntro(AssetManager.getMessage("tutorial_intro_title"), AssetManager.getMessage("tutorial_intro_content")),
                new TutorialEntry(TutorialEntry.FocusType.square_big, 1, -1, AssetManager.getMessage("tutorial_playerPoints_title"), AssetManager.getMessage("tutorial_playerPoints_content"), 220, 840, 500, 500),
                new TutorialEntry(TutorialEntry.FocusType.square_big, -1, -1, AssetManager.getMessage("tutorial_bagIndicator_title"), AssetManager.getMessage("tutorial_bagIndicator_content"), 1700, 840, 220, 840),
                new TutorialEntry(TutorialEntry.FocusType.rectangle_small, 1, 1, AssetManager.getMessage("tutorial_nameIndicator_title"), AssetManager.getMessage("tutorial_nameIndicator_content"), 450, 80, 1700, 840),
                new TutorialEntry(TutorialEntry.FocusType.rectangle_big, 1, -1, AssetManager.getMessage("tutorial_cardHotbar_title"), AssetManager.getMessage("tutorial_cardHotbar_content"), 945, 924, 450, 80),
                new TutorialEntry(TutorialEntry.FocusType.rectangle_large, 1, -1, AssetManager.getMessage("tutorial_playground_title"), AssetManager.getMessage("tutorial_playground_content"), SceneManager.getCurrentScene().getCenterX(), SceneManager.getCurrentScene().getCenterY(), 500, 500)
        );

        for (int i = 2; i <= entryCount + 1; i++) {
            sceneEntities.get(i).visible = false;
            sceneEntities.get(i).velocity = 0.01;
        }
    }

    private int entryStepIndex = 1;

    @Override
    public void onTick(double dt) {
        if (InputManager.isKeyPressed(KeyEvent.VK_ENTER)) {
            entryStepIndex++;
            for (int i = 1; i <= entryCount + 1; i++) {
                if (i != entryStepIndex) sceneEntities.get(i).visible = false;
                else {
                    sceneEntities.get(i).visible = true;
                    sceneEntities.get(i).velocity = entryVelocity;
                }
            }
            if (entryStepIndex == entryCount + 2) {
                AssetManager.playSound("tutorial/moveto_gameplay", 1.0);
                SceneManager.stopTempScene();
            }   else PlayRandomSound.playRand("btnclickpenta/B4", "btnclickpenta/Cis5", "btnclickpenta/E5", "btnclickpenta/Fis5", "btnclickpenta/Gis4");
        }
    }
}
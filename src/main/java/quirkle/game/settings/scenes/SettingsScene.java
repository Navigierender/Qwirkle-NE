package quirkle.game.settings.scenes;

import quirkle.PersistentData;
import quirkle.engine.*;
import quirkle.game.settings.entities.LanguageSwitchButton;
import quirkle.game.settings.entities.SoundVolumeSlider;
import quirkle.game.util.*;

import java.awt.*;
import java.awt.event.KeyEvent;

public class SettingsScene extends Scene {

    private RectangularButton returnButton;
    private RectangularButton applyButton;
    private PrlxEntity background;
    private LanguageSwitchButton languageSwitchButton;
    private SimpleTextEntity languageValueLabel;
    private SoundVolumeSlider volumeSlider;

    private static final int BUTTON_PADDING = 100;
    private static final int ROW_START_Y = 400;
    private static final int ROW_SPACING_Y = 90;
    private static final int LABEL_COLUMN_X = BUTTON_PADDING;
    private static final int CONTROL_COLUMN_X = 620;
    private static final int VALUE_COLUMN_X = 1050;

    private String originalLangIdentifier;

    @Override
    public void onCreate() {
        originalLangIdentifier = AssetManager.getLangIdentifier();

        returnButton = new RectangularButton(AssetManager.getMessage("return"));
        applyButton = new RectangularButton(AssetManager.getMessage("apply"));

        background = new PrlxEntity(
                new PrlxEntity.PrlxLayer("settings/background/0", 0.025),
                new PrlxEntity.PrlxLayer("settings/background/1", 0.05)
        );

        SimpleSpriteEntity uiLine = new SimpleSpriteEntity("settings/line", Entity.OriginPresets.CENTER);
        SimpleTextEntity title = new SimpleTextEntity(AssetManager.getMessage("settings"), 70, "higher_jump", Color.WHITE, Entity.OriginPresets.TOP_LEFT);
        SimpleTextEntity languageLabel = new SimpleTextEntity(AssetManager.getMessage("language"), 40, "poly_regular", Color.WHITE, Entity.OriginPresets.CENTER_LEFT);
        SimpleTextEntity volumeLabel = new SimpleTextEntity(AssetManager.getMessage("volume"), 40, "poly_regular", Color.WHITE, Entity.OriginPresets.CENTER_LEFT);

        languageSwitchButton = new LanguageSwitchButton();
        languageValueLabel = new SimpleTextEntity(AssetManager.getLangName(), 40, "poly_regular", Color.WHITE, Entity.OriginPresets.CENTER_LEFT);
        volumeSlider = new SoundVolumeSlider((int) Math.round(EngineConfig.VOLUME_MAIN * 100));

        addEntities(background, title, uiLine, languageLabel, languageSwitchButton, languageValueLabel, volumeLabel, volumeSlider, returnButton, applyButton);

        title.x = BUTTON_PADDING; title.y = BUTTON_PADDING / 2;

        uiLine.x = getCenterX(); uiLine.y = BUTTON_PADDING * 2.6;

        languageLabel.x = LABEL_COLUMN_X; languageLabel.y = ROW_START_Y;
        languageSwitchButton.x = CONTROL_COLUMN_X; languageSwitchButton.y = ROW_START_Y;
        languageValueLabel.x = VALUE_COLUMN_X; languageValueLabel.y = ROW_START_Y;

        volumeLabel.x = LABEL_COLUMN_X; volumeLabel.y = ROW_START_Y + ROW_SPACING_Y;
        volumeSlider.x = CONTROL_COLUMN_X; volumeSlider.y = ROW_START_Y + ROW_SPACING_Y;

        returnButton.x = BUTTON_PADDING + returnButton.width / 2; returnButton.y = getHeight() - BUTTON_PADDING;
        applyButton.x = getWidth() - (BUTTON_PADDING + applyButton.width / 2); applyButton.y = getHeight() - BUTTON_PADDING;
    }

    @Override
    public void onTick(double dt) {
        background.tarX = InputManager.getMouseX();
        background.tarY = InputManager.getMouseY();

        if (languageSwitchButton.isClicked()) {
            languageSwitchButton.cycleLanguage();
            languageValueLabel.setMessage(AssetManager.getLangName());
        }

        if (returnButton.isClicked() || InputManager.isKeyPressed(KeyEvent.VK_ESCAPE)) {
            AssetManager.setLang(originalLangIdentifier);
            SceneManager.stopTempScene();
        }

        if (applyButton.isClicked() || InputManager.isKeyPressed(KeyEvent.VK_ENTER)) {
            EngineConfig.VOLUME_MAIN = volumeSlider.getValue() / 100.0;
            PersistentData.lang = AssetManager.getLangIdentifier();
            SceneManager.setTempScene(new ReloadDialogBox(AssetManager.getMessage("reloadrequest")), false, true);
        }
    }
}

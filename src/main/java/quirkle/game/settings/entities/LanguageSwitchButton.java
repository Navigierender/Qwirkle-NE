package quirkle.game.settings.entities;

import quirkle.engine.AssetManager;
import quirkle.game.util.RectangularButton;

import java.util.List;

/**
 * A {@link RectangularButton} that cycles through {@link #AVAILABLE_LANGUAGES} and
 * loads the next one immediately via {@link AssetManager#setLang(String)}.
 * @note The button label itself stays static (e.g. "Wechsel"/"Switch"); the resulting
 *       language name is read separately via {@link AssetManager#getLangName()}.
 */
public class LanguageSwitchButton extends RectangularButton {

    private static final List<String> AVAILABLE_LANGUAGES = List.of("en", "de");

    public LanguageSwitchButton() {
        super(AssetManager.getMessage("switch"));
    }

    /** Advances to the next entry in {@link #AVAILABLE_LANGUAGES}, wrapping around, and loads it. */
    public void cycleLanguage() {
        List<String> languages = AVAILABLE_LANGUAGES;
        int currentIndex = languages.indexOf(AssetManager.getLangIdentifier());
        int nextIndex = (currentIndex + 1) % languages.size();
        AssetManager.setLang(languages.get(nextIndex));
    }
}

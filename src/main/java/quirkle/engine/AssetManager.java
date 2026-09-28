package quirkle.engine;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import quirkle.engine.extensions.LangPackage;

//temporary dynamic loading solutions will be swapped out later for a full precaching solution
public class AssetManager {

    private static final Map<String, BufferedImage> textureCache = new HashMap<>();
    private static BufferedImage fallbackTexture = null;

    public static BufferedImage getTexture(String identifier) {
        identifier = EngineConfig.ASSET_ORIGIN + EngineConfig.TEXTURE_SUBDIR + "/" + identifier + ".png"; //direct translation in prep for future precaching (frontend-shift)

        if (identifier == null || identifier.isEmpty()) return getFallbackTexture();
        if (textureCache.containsKey(identifier)) return textureCache.get(identifier);

        try (InputStream in = AssetManager.class.getResourceAsStream(identifier)) {
            if (in == null) {
                EngineConfig.message("Could not find texture: " + identifier, AssetManager.class.getSimpleName(), EngineConfig.messageType.WARNING);
                return getFallbackTexture();
            }
            BufferedImage img = ImageIO.read(in);
            textureCache.put(identifier, img);
            return img;
        } catch (Exception e) {
            EngineConfig.message("Failed to load texture: " + identifier, AssetManager.class.getSimpleName(), EngineConfig.messageType.ERROR);
            return getFallbackTexture();
        }
    }

    //Fallback textures only loads if AssetManager needs it
    private static BufferedImage getFallbackTexture() {
        if (fallbackTexture == null) {
            int size = 128;
            int halfSize = size / 2;

            fallbackTexture = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = fallbackTexture.createGraphics();
            g.setColor(Color.MAGENTA);
            g.fillRect(0, 0, halfSize, halfSize);
            g.fillRect(halfSize, halfSize, halfSize, halfSize);
            g.setColor(Color.BLACK);
            g.fillRect(halfSize, 0, halfSize, halfSize);
            g.fillRect(0, halfSize, halfSize, halfSize);
            g.dispose();
        }
        return fallbackTexture;
    }

    //----------[ FONT ]------------------------------------------------------------------------------------------------------------------------------------

    private static final Map<String, Font> fontCache = new HashMap<>();
    private static final Font fallbackFont = new Font("Arial", Font.PLAIN, 24);

    public static Font getFont(String identifier) {
        if (identifier == null || identifier.isEmpty()) return fallbackFont;

        identifier = EngineConfig.ASSET_ORIGIN + EngineConfig.FONT_SUBDIR + "/" + identifier + ".ttf"; //direct translation in prep for future precaching (frontend-shift)

        if (fontCache.containsKey(identifier)) return fontCache.get(identifier);

        try (InputStream in = AssetManager.class.getResourceAsStream(identifier)) {
            if (in == null) {
                EngineConfig.message("Could not find font: " + identifier, AssetManager.class.getSimpleName(), EngineConfig.messageType.WARNING);
                return fallbackFont;
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, in);
            fontCache.put(identifier, font);
            return font;
        } catch (Exception e) {
            EngineConfig.message("Failed to load font: " + identifier, AssetManager.class.getSimpleName(), EngineConfig.messageType.ERROR);
            return fallbackFont;
        }
    }

    //----------[ LANG ]------------------------------------------------------------------------------------------------------------------------------------

    private static LangPackage lang = null;
    private static String langIdentifier = null;

    public static void setLang(String identifier) {
        if (identifier == null || identifier.isEmpty()) {
            String errorMessage = "setLang was called with an empty identifier";
            if (lang == null) errorMessage += " -> THIS IS AN UNREPLACABLE UNCATCHED PROBLEM SINCE THE STANDART LANG COULD NOT BE LOADED!!!";
            EngineConfig.message(errorMessage, AssetManager.class.getSimpleName(), EngineConfig.messageType.ERROR);
            return;
        }

        String path = EngineConfig.ASSET_ORIGIN + EngineConfig.LANG_SUBDIR + "/" + identifier + ".lang";

        try (InputStream in = AssetManager.class.getResourceAsStream(path)) {
            if (in == null) {
                EngineConfig.message("could not load lang file at: " + path, AssetManager.class.getSimpleName(), EngineConfig.messageType.ERROR);
                return;
            }

            try (Scanner langReader = new Scanner(in)) {
                lang = new LangPackage(langReader);
                langIdentifier = identifier;
            }
        } catch (IOException e) {
            EngineConfig.message("failed to process lang file at " + path, AssetManager.class.getSimpleName(), EngineConfig.messageType.ERROR);
        }
    }

    public static String getMessage(String identifier) {
        if (lang.elements.containsKey(identifier)) return lang.elements.get(identifier);
        else return "CURRENT LANG DOESNT CONTAIN KEY: " + identifier;
    }

    /** @return the identifier last passed to {@link #setLang(String)} (e.g. "en", "de") */
    public static String getLangIdentifier() {
        return langIdentifier;
    }

    /** @return the display name declared on the first line of the currently loaded .lang file (e.g. "Deutsch") */
    public static String getLangName() {
        return lang != null ? lang.languageName : "";
    }

    //----------[ SOUND ]------------------------( credit reference https://stackoverflow.com/questions/26305/how-can-i-play-sound-in-java )--------------

    private static final Map<String, byte[]> soundCache = new HashMap<>();
    private static void fallbackSound(String identifier) {
        EngineConfig.message("Failed to play sound: " + identifier,  AssetManager.class.getSimpleName(), EngineConfig.messageType.ERROR);
    }

    private static void playRawBytes(byte[] soundData, double volume) {
        if (volume <= 0.0) return;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(soundData);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bais);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            //transform dB
            float dBvol = (float) (20 * Math.log10(volume * EngineConfig.VOLUME_MAIN)); //dB conversion
            dBvol = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dBvol));
            gainControl.setValue(dBvol);

            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) clip.close();
            });

            clip.start();

        }   catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            fallbackSound("not identifiable");
        }
    }

    public static void playSound(String identifier, double volume) {
        if (identifier == null || identifier.isEmpty()) { fallbackSound(identifier); return; }

        identifier = EngineConfig.ASSET_ORIGIN + EngineConfig.SOUND_SUBDIR + "/" + identifier + ".wav";

        if (soundCache.containsKey(identifier)) { playRawBytes(soundCache.get(identifier), volume); return; }

        try (InputStream in = AssetManager.class.getResourceAsStream(identifier)) {
            if (in == null) { fallbackSound(identifier); return; }
            byte[] soundData = in.readAllBytes();
            soundCache.put(identifier, soundData);
            playRawBytes(soundData, volume);
        } catch (Exception e) {
            fallbackSound(identifier);
        }
    }
}

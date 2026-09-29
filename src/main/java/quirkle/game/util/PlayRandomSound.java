package quirkle.game.util;

import org.navigierender.rslair.AssetManager;

public class PlayRandomSound {

    public static void playRand(String... soundIdentifiers) {
        int rand = (int) (Math.random() * soundIdentifiers.length);
        AssetManager.playSound(soundIdentifiers[rand], 1.0);
    }
}
package quirkle;

import org.navigierender.rslair.*;
import org.navigierender.rslair.tools.init;

import quirkle.game.startmenu.scenes.StartMenuScene;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {

        if (args.length > 0 && args[0].equalsIgnoreCase("debug")) {
            PersistentData.debugMode = true;
            Config.SUPPRES_WARNINGS = false;
            Config.SUPPRES_INFO = false;
        }

        init.init(new StartMenuScene());
    }
}

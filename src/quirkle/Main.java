package quirkle;

import quirkle.engine.*;
import quirkle.game.startmenu.scenes.StartMenuScene;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {

        if (args.length > 0 && args[0].equalsIgnoreCase("debug")) {
            PersistentData.debugMode = true;
            EngineConfig.SUPPRES_WARNINGS = false;
            EngineConfig.SUPPRES_INFO = false;
        }

        SwingUtilities.invokeLater(() -> {
            EngineConfig.setTitle("Qwirkle - Game");
            EngineConfig.setSize(1920, 1080);
            EngineConfig.BACKGROUND_COLOR = Color.BLACK;
            EngineConfig.FPS = 120;
            EngineConfig.DEFAULT_LANG_IDENTIFIER = "en";

            AssetManager.setLang(EngineConfig.DEFAULT_LANG_IDENTIFIER);

            JFrame frame = new JFrame(EngineConfig.TITLE);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(false); //disabling the frame
            frame.setResizable(false);

            SceneManager.setScene(new StartMenuScene());

            RenderPanel panel = new RenderPanel();
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            panel.requestFocusInWindow();

            GameLoop loop = new GameLoop(panel);
            loop.start();
        });
    }
}

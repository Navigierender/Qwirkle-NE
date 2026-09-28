package quirkle.engine;

import java.awt.Color;

/************************************************************************
 * Basic Default-Setup Class for resourceEngine presets
 ***********************************************************************/
public class EngineConfig {
    public final static String VERSION_INFO = "[BETA] v0.2.3";

    public static boolean SUPPRES_INFO = true;
    public static boolean SUPPRES_WARNINGS = false;

    public final static String ASSET_ORIGIN = "/assets";

    public static String TEXTURE_SUBDIR = "/textures";
    public static String FONT_SUBDIR = "/fonts";
    public static String SOUND_SUBDIR = "/sounds";
    public static String LANG_SUBDIR = "/lang";

    public static String DEFAULT_LANG_IDENTIFIER = "en";

    public static int WINDOW_WIDTH = 1920;
    public static int WINDOW_HEIGHT = 1080;
    public static String TITLE = "ResourceEngine - DEMO";
    public static Color BACKGROUND_COLOR = Color.WHITE;
    public static int FPS = 120;
    public static double VOLUME_MAIN = 1.0; //0.0 - 1.0 multiplier

    public static void setSize(int width, int height) {
        WINDOW_WIDTH = width;
        WINDOW_HEIGHT = height;
    }

    public static void setTitle(String title) {
        TITLE = title;
    }

    private static final String messageConstruct = "[%s] resourceEngine / %s: %s";

    public enum messageType {
        INFO("INFO"),
        WARNING("WARNING"),
        ERROR("ERROR"),
        CRITICAL_ERROR("CRITICAL ERROR");

        public final String label;
        private messageType(String label) { this.label = label; }
    }
    
    public static void message(String message, String author, messageType type) {
        String messageResult = String.format(messageConstruct, type.label, author, message);
        if (type == messageType.INFO) {
            if (!SUPPRES_INFO) System.out.println(messageResult);
        } else {
            if (!SUPPRES_WARNINGS) System.err.println(messageResult);
        }
    }
}

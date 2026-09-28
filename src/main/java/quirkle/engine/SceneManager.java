package quirkle.engine;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;

public class SceneManager {
    //allows basic scene request at Entity initilisation by initialising currentScene as a Dummy Scene
    private static Scene currentScene = new Scene();
    private static Scene storedScene;

    public static void setScene(Scene newScene) {
        if (currentScene != null) currentScene.destroy();

        currentScene = newScene;
        currentScene.create();

        if (storedScene != null) {
            storedScene.destroy();
            storedScene = null;
        }
    }

    private static boolean renderStoredScene;
    private static boolean pauseStoredScene;

    public static float currentSceneAlpha = 1.0f;
    public static float storedSceneAlpha = 1.0f;

    /**
     * Allows to load a temporary Scene that can exist without unloading the {@link #currentScene} / {@link #storedScene}.
     * When this is called but there is already a temporary scenes loaded the temporary Scene will be destroyed and overwritten by the new temporary Scene.
     * @param tempScene Scene to load as temporary Scene
     * @param _renderStoredScene continue to call {@link quirkle.engine.Scene#render()} Event of the stored Scene
     * @param _pauseStoredScene pause the {@link #storedScene}s {@link quirkle.engine.Scene#update()} Event
     */
    public static void setTempScene(Scene tempScene, boolean _renderStoredScene, boolean _pauseStoredScene) {
        if (!(storedScene == null)) {
            EngineConfig.message("overwriting already stored temporary Scene", SceneManager.class.getSimpleName(), EngineConfig.messageType.INFO);

            currentScene.destroy();
            currentScene = tempScene;
        }   else    {
            storedScene = currentScene;
            currentScene = tempScene;
        }

        currentScene.create();

        renderStoredScene = _renderStoredScene;
        pauseStoredScene = _pauseStoredScene;

        EngineConfig.message("loaded temporary scene: " + tempScene.getClass().getSimpleName(), SceneManager.class.getSimpleName(), EngineConfig.messageType.INFO);
    }

    /**
     * Destroyes the current temporary Scene and loads the current Scene as the main Scene
     */
    public static void stopTempScene() {
        if (storedScene != null) {
            currentScene.destroy();
            currentScene = storedScene;
            storedScene = null;
        }   else    {
            EngineConfig.message("cant return to stored Scene! There is no stored Scene", SceneManager.class.getSimpleName(), EngineConfig.messageType.CRITICAL_ERROR);
        }
    }

    public static Scene getCurrentScene() {
        return currentScene;
    }

    public static void update(double dt) {
        if (currentScene != null) currentScene.update(dt);
        if (!pauseStoredScene && storedScene != null) storedScene.update(dt);

        InputManager.endFrame();
    }

    public static void render(Graphics2D g) {
        if (renderStoredScene && storedScene != null) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, storedSceneAlpha));
            storedScene.render(g);
        }

        if (currentScene != null) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, currentSceneAlpha));
            currentScene.render(g);
        }
    }
}
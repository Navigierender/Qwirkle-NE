package quirkle.engine;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Scene {
    protected final List<Entity> sceneEntities = new CopyOnWriteArrayList<>();
    public boolean created = false;

    public int getWidth() { return EngineConfig.WINDOW_WIDTH; }
    public int getHeight() { return EngineConfig.WINDOW_HEIGHT; }
    public int getCenterX() { return (int) (getWidth() / 2.0); }
    public int getCenterY() { return (int) (getHeight() / 2.0); }

    /**
     * Called automatically during Scene creation
     * Use this to initialize custom properties
     * @note Executed inside the base constructor layout
     */
    public void onCreate() {}

    /**
     * Executes core game logic updates once per frame loop
     * @param dt The delta time step value in seconds
     * @note Driven automatically by the central {@link #update} function
     */
    public void onTick(double dt) {}

    /**
     * Performs custom entity layer drawing operations
     * @param g The active Graphics2D rendering context
     * @note Executed immediately after the sprite textures of the sceneEntities get drawn
     */
    public void onRender(Graphics2D g) {}

    /**
     * Handles custom cleanup operations right before scene removal
     */
    public void onDestroy() {}

    /**
     * Adds given {@link Entity}'s to the Scenes {@link #sceneEntities}-CopyOnWriteArrayList in given Order
     * @param entities n >= 1 Entity Objects that should be added to the scene
     * @note Render Event will paint Entities in creation Order (from Oldest [bottom] to youngest [top])
     */
    public void addEntities(Entity... entities) { //really nice solution i found here https://www.geeksforgeeks.org/java/variable-arguments-varargs-in-java/
        for (Entity e : entities) {
            e.create();
            this.sceneEntities.add(e); // Note: Make sure the list name matches (entities vs sceneEntities)
            EngineConfig.message(getClass().getSimpleName() + ": added: " + e.getClass().getSimpleName(), getClass().getSimpleName(), EngineConfig.messageType.INFO);
        }
    }

    /**
     * Removs given {@link Entity}'s from the Scenes {@link #sceneEntities}-CopyOnWriteArrayList
     * @param entities Entity Objects that should be removed from the scene
     * @note utilizing this will affect paint Order in the render event
     */
    public void removeEntities(Entity... entities) {
        for (Entity e : entities) {
            e.destroy();
            sceneEntities.remove(e);
        }
    }

    public void create() {
        if (created) return;
        created = true;
        try {
            onCreate();
        } catch (Throwable t) {
            EngineConfig.message("Throwable Error in onCreate() of " + getClass().getSimpleName() + ": " + t.getMessage(), getClass().getSimpleName(), EngineConfig.messageType.ERROR);
        }
    }

    public void update(double dt) {
        onTick(dt);
        for (Entity entity : sceneEntities) {
            if (!entity.destroyed) {
                entity.update(dt);
            }
        }
        sceneEntities.removeIf(entity -> entity.destroyed);
    }

    public void render(Graphics2D g) {
        onRender(g);
        for (Entity entity : getRenderOrderedEntities()) {
            entity.render(g);
        }
    }

    /**
     * @return the scene's entities ordered by {@link Entity#renderOrder}, lowest first
     * @note Sorts a copy rather than {@link #sceneEntities} itself, since reordering a
     *  copyOnWriteArrayList copies the whole backing array on every write.
     * @implNote {@link List#sort} is stable, so entities sharing a renderOrder keep the
     *  order they were added in
     */
    private List<Entity> getRenderOrderedEntities() {
        List<Entity> ordered = new ArrayList<>(sceneEntities);
        ordered.sort(Comparator.comparingInt(entity -> entity.renderOrder));
        return ordered;
    }

    public void destroy() {
        onDestroy();
        for (Entity entity : sceneEntities) {
            entity.destroy();
        }
        sceneEntities.clear();
    }
}

package quirkle.engine;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/*********************************************************************************************
 * An Entity is a transformable object managed withing a Scene,
 * providing basic lifecycle hooks for
 * initilisation ticking, rendering  and destruction,
 * alongside built in support for positioning, rotation, interpolation and mouse detection
 *
 * @note must be registered to a {@link Scene} using {@link Scene#add(Entity)}
 *       to activly receive update and render steps
 ********************************************************************************************/
public abstract class Entity {
    public enum OriginPresets {
        TOP_LEFT(0.0, 0.0),
        TOP_MID(0.5, 0.0),
        TOP_RIGHT(1.0, 0.0),
        CENTER_LEFT(0.0,0.5),
        CENTER(0.5, 0.5),
        CENTER_RIGHT(1.0, 0.5),
        BOTTOM_LEFT(0.0, 1.0),
        BOTTOM_MID(0.5, 1.0),
        BOTTOM_RIGHT(1.0, 1.0);

        public final double x;
        public final double y;

        OriginPresets(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * The Origin of an entity is the anchor point
     * used as the absolute reference for its position,
     * rotation and scaling in the Scene
     * @see OriginPresets
     * */
    public OriginPresets origin = OriginPresets.TOP_LEFT;

    public double x = 0;
    public double y = 0;
    public double targetX = 0;
    public double targetY = 0;
    public double rotation = 0;

    /**
     * velocity is used to interpolate consistently across
     * different FPS values using deltaTime
     * @note if set to 0 movement will become instant
     * @see #moveToTarget
     */
    public double velocity = 0;

    public double width = 0;
    public double height = 0;
    public double scale = 1.0;

    public int renderOrder = 0;

    public String spritePath;
    public BufferedImage sprite;
    public boolean visible = true;
    public boolean destroyed = false;
    public boolean created = false;

    // Lifecycle hooks

    /**
     * Called automatically during entity instantion
     * Use this to initialize custom entity states or properties
     * @note Executed inside the base constructor layout [might change in the future -> correct instantation after beeing added to scene]
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
     * @note Executed immediately after the base sprite sprite gets drawn
     */
    public void onRender(Graphics2D g) {}

    /**
     * Handles custom cleanup operations right before entity removal
     * @note Invoked immediately after the {@link #destroyed} flag flips is set to true
     */
    public void onDestroy() {}

    /**
     * Simple Method to set a Entity bound texture that also acts as the entities "hitbox"
     * @param identifier texture Identifier (view: {@link AssetManager#getTexture(String)})
     */
    public void setSprite(String identifier) {
        this.spritePath = identifier;
        this.sprite = AssetManager.getTexture(identifier);
        this.width = this.sprite.getWidth();
        this.height = this.sprite.getHeight();
    }

    public int getScaledWidth() {
        return (int) (width * scale);
    }

    public int getScaledHeight() {
        return (int) (height * scale);
    }

    /**
     * Helper function to check if the Entity is hovered by the mouse.
     * only works if the Entity has a main Sprite, set by {@link #setSprite()}
     * @return
     */
    public boolean isHovered() {
        //full credit: https://web.archive.org/web/20100430183237/https://ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
        //inverse mouse rotation and then square checkk

        double mouseX = InputManager.getMouseX();
        double mouseY = InputManager.getMouseY();

        double scaledWidth = getScaledWidth();
        double scaledHeight = getScaledHeight();

        // Origin implied adjustion calculations (to swings top left based drawing)
        double drawX = x - origin.x * scaledWidth;
        double drawY = y - origin.y * scaledHeight;

        if (rotation % 360 != 0) {
            double rad = Math.toRadians(rotation);
            double cos = Math.cos(rad);
            double sin = Math.sin(rad);

            // Translate mouse relative to origin (x, y)
            double translatedX = mouseX - x;
            double translatedY = mouseY - y;

            //rotating mouse coords inversly around the origin
            mouseX = translatedX * cos + translatedY * sin + x;
            mouseY = -translatedX * sin + translatedY * cos + y;
        }

        return mouseX >= drawX && mouseX <= drawX + scaledWidth && mouseY >= drawY && mouseY <= drawY + scaledHeight;
    }

    public boolean isClicked() {
        return isHovered() && InputManager.isMouseClicked();
    }

    /**
     * velocity is used to interpolate consistently across
     * different FPS values using delta time
     * move to {@link #targetX} and {@link #targetY} dynamicly calculation movement distance using deltaTime
     * @param dt DeltaTime
     * @see #velocity
     */
    public void moveToTarget(double dt) {
        if (velocity <= 0) {
            x = targetX;
            y = targetY;
            return;
        }

        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        double maxDist = velocity * dt;

        if (dist <= maxDist || dist == 0) {
            x = targetX;
            y = targetY;
        } else {
            x += (dx / dist) * maxDist;
            y += (dy / dist) * maxDist;
        }
    }

    /**
     * Checks whether the {@link Entity} has already reached its current destination.
     * Useful for starting a new phase after a movement phase
     * (e.g. setting a new destination or destroying the entity).
     */
    public boolean hasArrivedAtTarget() {
        return x == targetX && y == targetY;
    }

    public void drawText(String msg, float fontSize, Color color, String fontIdentifier, double x, double y, double rotation, OriginPresets origin, Graphics2D g) {
        Font font = AssetManager.getFont(fontIdentifier).deriveFont(fontSize);
        g.setFont(font);

        FontMetrics fm = g.getFontMetrics(font);

        // Split message by backslash to support multiline text
        String[] lines = msg.split("\\\\", -1);

        // Calculate total height and max width
        int lineHeight = fm.getHeight();
        int totalHeight = lineHeight * lines.length;
        int maxWidth = 0;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, fm.stringWidth(line));
        }

        int textAscent = fm.getAscent();

        int drawX = (int) (x - (origin.x * maxWidth));
        int drawY = (int) (y - (origin.y * totalHeight) + textAscent);

        Graphics2D isoTextGraphic = (Graphics2D) g.create();
        isoTextGraphic.setFont(font);
        isoTextGraphic.setColor(color);

        if (rotation % 360 != 0) {
            isoTextGraphic.rotate(Math.toRadians(rotation), x, y);
        }

        // Draw each line
        for (int i = 0; i < lines.length; i++) {
            isoTextGraphic.drawString(lines[i], drawX, drawY + (i * lineHeight));
        }

        isoTextGraphic.dispose();
    }

    /**
     * Helper function to draw Sprites. Should be used inside the {@link #onRender()} hook.
     * @param spriteIdentifier sprite name including possible subdir inside {@link EngineConfig#TEXTURE_SUBDIR} without filetype suffix
     * @param scale scale multiplier (scale = 1.0: rendering the texture at its original scale)
     * @param x coordinate
     * @param y coordinate
     * @param rotation rotation in degrees
     * @param origin origin of the texture element of type {@link OriginPresets}
     * @param g Graphics2D (isolated child is created inside the function itself)
     */
    public void drawSprite(String spriteIdentifier, double scale, double x, double y, double rotation, OriginPresets origin, Graphics2D g) {
        BufferedImage sprite = AssetManager.getTexture(spriteIdentifier);
        Graphics2D isoSpriteGraphic = (Graphics2D) g.create();
        double scaledWidth = sprite.getWidth() * scale;
        double scaledHeight = sprite.getHeight() * scale;

        int drawX = (int) (x - origin.x * scaledWidth);
        int drawY = (int) (y - origin.y * scaledHeight);

        isoSpriteGraphic.rotate(Math.toRadians(rotation), x, y);
        isoSpriteGraphic.drawImage(sprite, drawX, drawY, (int) scaledWidth, (int) scaledHeight, null);

        isoSpriteGraphic.dispose();
    }

    /**
     * handles entity initialisation by calling the {@link #onCreate()} hook, catching errors if necessary.
     * Is called after the related Scene called {@link Scene#addEntities()}
     */
    public void create() {
        if (created) return;
        created = true;
        try {
            onCreate();
        } catch (Throwable t) {
            EngineConfig.message("Throwable Error in onCreate() of " + getClass().getSimpleName(), getClass().getSimpleName(), EngineConfig.messageType.ERROR);
        }
    }

    /**
     * updates the Entity by calling the {@link #onTick()} hook, catching errors if necessary.
     * Is skipped if the Entity is {@link #destroyed}
     * @param dt
     */
    public void update(double dt) {
        if (destroyed) return;

        try {
            onTick(dt);
        } catch (Throwable t) {
            EngineConfig.message("Throwable Error in onTick() of " + getClass().getSimpleName() + ": " + t.getMessage(), getClass().getSimpleName(), EngineConfig.messageType.ERROR);
        }
    }

    /**
     * renders if {@link #visible} is true and the entity isnt {@link #destroyed}.
     * Also calls the {@link #onRender()} hook
     */
    public void render(Graphics2D g) {
        if (!visible || destroyed) return;

        double scaledWidth = getScaledWidth();
        double scaledHeight = getScaledHeight();

        int drawX = (int) (x - origin.x * scaledWidth);
        int drawY = (int) (y - origin.y * scaledHeight);

        //isolation for gSprite transformations
        if (sprite != null) {
            Graphics2D gSprite = (Graphics2D) g.create();
            gSprite.rotate(Math.toRadians(rotation), x, y);
            gSprite.drawImage(sprite, drawX, drawY, (int) scaledWidth, (int) scaledHeight, null);
            gSprite.dispose();
        }

        try {
            onRender(g);
        } catch (Throwable t) {
            EngineConfig.message("Throwable Error in onRender() of " + getClass().getSimpleName() + ": " + t.getMessage(), getClass().getSimpleName(), EngineConfig.messageType.ERROR);
        }
    }

    public void destroy() {
        if (!destroyed) {

            destroyed = true;
            try {
                onDestroy();
                destroyed = true;
            } catch (Throwable t) {
                EngineConfig.message("Throwable Error in onDestroy() of " + getClass().getSimpleName() + ": " + t.getMessage(), getClass().getSimpleName(), EngineConfig.messageType.ERROR);
            }
        }
    }
}
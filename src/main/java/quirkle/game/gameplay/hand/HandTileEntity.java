package quirkle.game.gameplay.hand;

import quirkle.game.gameplay.tiles.Tile;
import quirkle.game.gameplay.tiles.TileEntity;

import java.awt.*;

/**
 * A tile on the rack.
 * @note selection is shown by the frame in {@link TileRack}, this class only adds the shake animation
 */
public class HandTileEntity extends TileEntity {

    private static final double REJECTION_DURATION = 0.35;
    private static final double SHAKE_FREQUENCY = 55.0;
    private static final double SHAKE_AMPLITUDE = 9.0;

    private boolean selected = false;

    private double rejectionTimer = 0.0;

    public HandTileEntity(Tile tile) {
        super(tile);
    }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    public void reject() {
        rejectionTimer = REJECTION_DURATION;
    }

    public boolean isRejecting() {
        return rejectionTimer > 0.0;
    }

    @Override
    public void onTick(double dt) {
        if (rejectionTimer > 0.0) {
            rejectionTimer = Math.max(0.0, rejectionTimer - dt);
        }
    }

    @Override
    public void render(Graphics2D g) {
        double restingX = x;
        x += getShakeOffset();
        super.render(g);
        x = restingX;
    }

    private int getShakeOffset() {
        if (!isRejecting()) return 0;

        double decay = rejectionTimer / REJECTION_DURATION;
        double elapsed = REJECTION_DURATION - rejectionTimer;
        return (int) Math.round(Math.sin(elapsed * SHAKE_FREQUENCY) * SHAKE_AMPLITUDE * decay);
    }
}

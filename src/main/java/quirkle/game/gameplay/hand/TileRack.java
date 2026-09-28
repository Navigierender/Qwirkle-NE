package quirkle.game.gameplay.hand;

import quirkle.engine.AssetManager;
import quirkle.engine.InputManager;
import quirkle.engine.extensions.RecolorUtil;
import quirkle.game.gameplay.player.Player;
import quirkle.game.gameplay.tiles.Tile;
import quirkle.game.gameplay.ui.Handover;
import quirkle.game.gameplay.ui.PanelEntity;
import quirkle.game.gameplay.ui.UiTheme;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

public class TileRack extends PanelEntity {

    private final HandTileEntity[] slots = new HandTileEntity[Player.HAND_SIZE];

    private BufferedImage slotFrame;
    private BufferedImage selectedSlotFrame;
    private BufferedImage rejectedSlotFrame;

    private final int restingCenterX;
    private final int restingCenterY;

    private final Handover handover;

    private Player player;
    private Tile selectedTile;

    public TileRack(int centerX, int centerY, Handover handover) {
        this.restingCenterX = centerX;
        this.restingCenterY = centerY;
        this.handover = handover;
    }

    @Override
    public void onCreate() {
        // tint once here instead of every frame
        slotFrame = AssetManager.getTexture(UiTheme.SPRITE_SLOT_FRAME);
        selectedSlotFrame = RecolorUtil.tint(slotFrame, UiTheme.SELECTION);
        rejectedSlotFrame = RecolorUtil.tint(slotFrame, UiTheme.REJECTION);

        setBounds(restingCenterX, restingCenterY, rackWidth(), UiTheme.RACK_FRAME_SIZE, OriginPresets.CENTER);
    }

    public void showPlayer(Player currentPlayer) {
        if (player == null) {                       // first frame: there is nothing to hand over from
            takeOver(currentPlayer);
            return;
        }

        if (handover.isRunning()) {
            // switch the tiles while the rack is off screen
            if (handover.getPhase() == Handover.Phase.ARRIVING && player != currentPlayer) {
                takeOver(currentPlayer);
            }
            return;
        }

        // in case the turn changed without the animation
        if (player != currentPlayer) takeOver(currentPlayer);

        syncSlots(currentPlayer.getHand());

        if (!currentPlayer.getHand().contains(selectedTile)) selectedTile = null;
        applySelection();
    }

    private boolean isChangingHands() {
        return handover.isRunning();
    }

    public Tile getSelectedTile() {
        return selectedTile;
    }

    public void clearSelection() {
        select(null);
    }

    public void rejectSelection() {
        HandTileEntity tileEntity = findEntity(selectedTile);
        if (tileEntity != null) tileEntity.reject();
    }

    public boolean handleInput() {
        // ignore clicks while the rack is moving
        if (isChangingHands()) return false;
        if (!InputManager.isMouseClicked()) return false;

        for (HandTileEntity tileEntity : slots) {
            if (tileEntity == null || !tileEntity.isHovered()) continue;

            // clicking the selected tile again deselects it
            select(tileEntity.isSelected() ? null : tileEntity.getTile());
            return true;
        }

        return isHovered();
    }

    @Override
    public void onTick(double dt) {
        applySlide();
        layoutSlots();

        for (HandTileEntity tileEntity : slots) {
            if (tileEntity != null) tileEntity.update(dt);
        }
    }

    private void applySlide() {
        if (!handover.isRunning()) {
            y = restingCenterY;
            return;
        }

        int hiddenCenterY = UiTheme.rackHiddenCenterY();
        double progress = handover.getPhaseProgress();

        y = handover.getPhase() == Handover.Phase.LEAVING
                ? Handover.at(restingCenterY, hiddenCenterY, progress)
                : Handover.at(hiddenCenterY, restingCenterY, progress);
    }

    private void takeOver(Player newPlayer) {
        this.player = newPlayer;
        Arrays.fill(slots, null);

        syncSlots(newPlayer.getHand());

        selectedTile = null;
        applySelection();
    }

    private void layoutSlots() {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] != null) layoutSlot(i);
        }
    }

    @Override
    public void onRender(Graphics2D g) {
        // every slot gets a frame, also the empty ones
        for (int i = 0; i < slots.length; i++) {
            drawSlotFrame(g, i);
        }

        for (HandTileEntity tileEntity : slots) {
            if (tileEntity != null) tileEntity.render(g);
        }
    }

    private void drawSlotFrame(Graphics2D g, int index) {
        int size = UiTheme.RACK_FRAME_SIZE;
        int left = (int) getSlotX(index) - size / 2;
        int top = getRestingY() - size / 2;

        g.drawImage(slotFrameFor(index), left, top, size, size, null);
    }

    /** @return red frame if the placement failed, yellow if selected, otherwise the normal one */
    private BufferedImage slotFrameFor(int index) {
        HandTileEntity tileEntity = slots[index];
        if (tileEntity == null) return slotFrame;

        if (tileEntity.isRejecting()) return rejectedSlotFrame;

        return tileEntity.isSelected() ? selectedSlotFrame : slotFrame;
    }

    private void syncSlots(List<Tile> hand) {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] != null && !hand.contains(slots[i].getTile())) slots[i] = null;
        }

        for (Tile tile : hand) {
            if (findEntity(tile) != null) continue;

            int freeSlot = firstFreeSlot();
            if (freeSlot < 0) break;

            slots[freeSlot] = new HandTileEntity(tile);
            slots[freeSlot].create();
        }
    }

    private void layoutSlot(int index) {
        HandTileEntity tileEntity = slots[index];

        tileEntity.setTileSize(UiTheme.RACK_TILE_SIZE);
        tileEntity.x = getSlotX(index);
        tileEntity.y = getRestingY();
    }

    private void select(Tile tile) {
        this.selectedTile = tile;
        applySelection();
    }

    private void applySelection() {
        for  (HandTileEntity tileEntity : slots) {
            if (tileEntity == null) continue;

            tileEntity.setSelected(tileEntity.getTile() == selectedTile);
        }
    }

    private HandTileEntity findEntity(Tile tile) {
        if (tile == null) return null;

        for (HandTileEntity tileEntity : slots) {
            if (tileEntity != null && tileEntity.getTile() == tile) return tileEntity;
        }
        return null;
    }

    private int firstFreeSlot() {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == null) return i;
        }
        return -1;
    }

    private double getSlotX(int index) {
        double firstSlotX = x - (Player.HAND_SIZE - 1) * UiTheme.RACK_TILE_SPACING / 2;
        return firstSlotX + index * UiTheme.RACK_TILE_SPACING;
    }

    private int getRestingY() {
        return (int) y;
    }

    /** @return width of all slot frames (also used as hover area) */
    private static int rackWidth() {
        return (Player.HAND_SIZE - 1) * UiTheme.RACK_TILE_SPACING + UiTheme.RACK_FRAME_SIZE;
    }
}

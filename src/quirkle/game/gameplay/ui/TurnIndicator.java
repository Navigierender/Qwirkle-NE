package quirkle.game.gameplay.ui;

import quirkle.engine.AssetManager;
import quirkle.engine.Entity;
import quirkle.game.gameplay.Game;
import quirkle.game.gameplay.player.Player;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Shows the turn order at the top. The current player is big, the others are smaller behind.
 * @note when the turn changes all names slide one slot to the left and the finished player moves to the end
 */
public class TurnIndicator extends Entity {

    /** position, font size and alpha of one name */
    private record Slot(double x, double y, double fontSize, double alpha) {

        static Slot between(Slot from, Slot to, double progress) {
            return new Slot(at(from.x, to.x, progress), at(from.y, to.y, progress), at(from.fontSize, to.fontSize, progress), at(from.alpha, to.alpha, progress));
        }

        private static double at(double from, double to, double progress) {
            return from + (to - from) * progress;
        }
    }

    private final Game game;

    /** the order that is shown (while animating it's the new order) */
    private List<Player> shownOrder;

    private List<Player> previousOrder = List.of();

    private double shuffleRemaining = 0.0;

    public TurnIndicator(Game game) {
        this.game = game;
        this.renderOrder = UiTheme.LAYER_HUD;

        this.y = UiTheme.TURN_TOP;
    }

    @Override
    public void onCreate() {
        // set here already, the first render can happen before the first tick
        shownOrder = game.getTurnOrder();
    }

    @Override
    public void onTick(double dt) {
        if (shuffleRemaining > 0.0) shuffleRemaining = Math.max(0.0, shuffleRemaining - dt);

        List<Player> order = game.getTurnOrder();
        if (order.equals(shownOrder)) return;

        previousOrder = shownOrder;
        shownOrder = order;
        shuffleRemaining = UiTheme.TURN_SHUFFLE_SECONDS;
    }

    @Override
    public void onRender(Graphics2D g) {
        if (shownOrder.isEmpty()) return;

        List<Slot> target = layout(g, shownOrder);

        if (shuffleRemaining <= 0.0) {
            for (int i = 0; i < shownOrder.size(); i++) {
                draw(g, shownOrder.get(i), target.get(i), 1.0);
            }
            return;
        }

        double progress = shuffleProgress();
        Player finished = finishedPlayer();

        // old row + the finished player at the end, so their name comes in from behind the row
        List<Slot> previous = layout(g, rowToSlideFrom(finished));

        for (int i = 0; i < shownOrder.size(); i++) {
            Player player = shownOrder.get(i);

            int previousSlot = player == finished
                    ? previous.size() - 1
                    : previousOrder.indexOf(player);

            // wasn't in the row before -> just fade in
            if (previousSlot < 0) {
                draw(g, player, target.get(i), progress);
                continue;
            }

            // only the player coming in at the end fades in
            double fade = player == finished ? progress : 1.0;
            draw(g, player, Slot.between(previous.get(previousSlot), target.get(i), progress), fade);
        }

        if (finished != null) drawFinished(g, finished, previous, progress);
    }

    /** moves the name of the finished player out to the left, it gets smaller and fades out */
    private void drawFinished(Graphics2D g, Player finished, List<Slot> previous, double progress) {
        Slot from = previous.get(0);

        // slot 1 already has the small size and the y position we need
        Slot trailing = previous.get(1);
        Slot out = new Slot(from.x() - UiTheme.TURN_LEAVE_SHIFT, trailing.y(),
                trailing.fontSize(), from.alpha());

        draw(g, finished, Slot.between(from, out, progress), 1.0 - progress);
    }

    /** @return previousOrder with the finished player added at the end again */
    private List<Player> rowToSlideFrom(Player finished) {
        if (finished == null) return previousOrder;

        List<Player> row = new ArrayList<>(previousOrder);
        row.add(finished);
        return row;
    }

    /**
     * @return the player whose turn just ended, or null if the order did not just move by one (can happen in debug mode)
     */
    private Player finishedPlayer() {
        if (previousOrder.size() < 2) return null;

        Player finished = previousOrder.get(0);
        return shownOrder.get(shownOrder.size() - 1) == finished ? finished : null;
    }

    /** @return animation progress from 0 to 1 (smoothstep) */
    private double shuffleProgress() {
        double linear = 1.0 - shuffleRemaining / UiTheme.TURN_SHUFFLE_SECONDS;
        return linear * linear * (3.0 - 2.0 * linear);
    }

    /** @return the slot of every name, the first one big and the rest small */
    private List<Slot> layout(Graphics2D g, List<Player> order) {
        List<Slot> slots = new ArrayList<>(order.size());

        int baseline = (int) y + metrics(g, UiTheme.FONT_SIZE_TURN_CURRENT).getAscent();
        double x = UiTheme.TURN_MARGIN_X;

        for (int i = 0; i < order.size(); i++) {
            boolean isLeading = i == 0;
            float fontSize = isLeading ? UiTheme.FONT_SIZE_TURN_CURRENT : UiTheme.FONT_SIZE_TURN_NEXT;

            // all names use the baseline of the first name
            double top = baseline - metrics(g, fontSize).getAscent();
            double alpha = isLeading ? UiTheme.TEXT.getAlpha() : UiTheme.TEXT_DIMMED.getAlpha();

            slots.add(new Slot(x, top, fontSize, alpha));

            x += textWidth(g, order.get(i).getName(), fontSize)
                    + (isLeading ? UiTheme.TURN_CURRENT_GAP : UiTheme.TURN_NAME_GAP);
        }

        return slots;
    }

    /** @param fade gets multiplied with the alpha of the slot */
    private void draw(Graphics2D g, Player player, Slot slot, double fade) {
        int alpha = (int) Math.round(Math.clamp(slot.alpha() * fade, 0.0, 255.0));
        Color color = new Color(UiTheme.TEXT.getRed(), UiTheme.TEXT.getGreen(), UiTheme.TEXT.getBlue(), alpha);

        drawText(player.getName(), (float) slot.fontSize(), color, UiTheme.FONT,
                slot.x(), slot.y(), 0.0, OriginPresets.TOP_LEFT, g);
    }

    private static int textWidth(Graphics2D g, String text, float fontSize) {
        return metrics(g, fontSize).stringWidth(text);
    }

    private static FontMetrics metrics(Graphics2D g, float fontSize) {
        Font font = AssetManager.getFont(UiTheme.FONT).deriveFont(fontSize);
        return g.getFontMetrics(font);
    }
}

package quirkle.game.gameplay.ui;

public class Handover {

    public enum Phase {
        IDLE,
        LEAVING,
        ARRIVING
    }

    private double elapsed;

    private boolean running;

    public void start() {
        elapsed = 0.0;
        running = true;
    }

    public void advance(double dt) {
        if (!running) return;

        elapsed += dt;
        if (elapsed >= 2.0 * UiTheme.HANDOVER_PHASE_SECONDS) running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public Phase getPhase() {
        if (!running) return Phase.IDLE;

        return elapsed < UiTheme.HANDOVER_PHASE_SECONDS ? Phase.LEAVING : Phase.ARRIVING;
    }

    public double getPhaseProgress() {
        if (!running) return 1.0;

        double half = UiTheme.HANDOVER_PHASE_SECONDS;
        double linear = clamp01(elapsed < half ? elapsed / half : elapsed / half - 1.0);

        return getPhase() == Phase.LEAVING ? easeIn(linear) : easeOut(linear);
    }

    public static double at(double from, double to, double progress) {
        return from + (to - from) * progress;
    }

    private static double easeIn(double t) {
        return t * t;
    }

    private static double easeOut(double t) {
        double remaining = 1.0 - t;
        return 1.0 - remaining * remaining;
    }

    private static double clamp01(double value) {
        return Math.clamp(value, 0.0, 1.0);
    }
}

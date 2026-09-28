package quirkle.engine;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameLoop implements ActionListener {
    private final RenderPanel gamePanel;
    private final Timer timer;

    private static final double WARNING_THRESHOLD_MILLIS = 250.0; // 1/4 seconds
    private static final double MAX_DT_SECONDS = 1.0;

    private final double expectedIntervalMillis;

    //temporary Note: fixing a problem where the Engine would return a giant tick behind warning because lastTickNanoSec wasnt really initialised
    private long lastTickNanoSec = System.nanoTime();

    public GameLoop(RenderPanel gamePanel) {
        this.gamePanel = gamePanel;
        this.expectedIntervalMillis = 1000 / EngineConfig.FPS;
        this.timer = new Timer( 1000 / EngineConfig.FPS, this);
        EngineConfig.message("started", getClass().getSimpleName(), EngineConfig.messageType.INFO);
    }

    public void start() { timer.start(); }
    public void stop() { timer.stop(); }

    @Override
    public void actionPerformed(ActionEvent event) {
        long now = System.nanoTime();
        double dt = (now - lastTickNanoSec) / 1_000_000_000.0;
        lastTickNanoSec = now;

        double realMilliSec = dt * 1000;
        double behindByMilliSec = realMilliSec - expectedIntervalMillis;

        if (behindByMilliSec > WARNING_THRESHOLD_MILLIS) {
            EngineConfig.message("tick behind by " + behindByMilliSec + "ms", getClass().getSimpleName(), EngineConfig.messageType.WARNING);
        }

        if (dt > MAX_DT_SECONDS) {
            EngineConfig.message("deltaTime clamped from " + realMilliSec + "ms to  " + MAX_DT_SECONDS*1000.0 + "ms",  getClass().getSimpleName(), EngineConfig.messageType.WARNING);
            dt = MAX_DT_SECONDS;
        }

        SceneManager.update(dt);
        gamePanel.repaint();
    }
}
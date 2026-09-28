package quirkle.engine;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class RenderPanel extends JPanel {

    public RenderPanel() {
        setPreferredSize(new Dimension(EngineConfig.WINDOW_WIDTH, EngineConfig.WINDOW_HEIGHT));
        setBackground(EngineConfig.BACKGROUND_COLOR);
        setFocusable(true);

        InputManager input = InputManager.getInstance();
        addMouseListener(input);
        addMouseMotionListener(input);
        addMouseWheelListener(input);
        addKeyListener(InputManager.getKeyHandler());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        SceneManager.render(g2d);
        g2d.dispose();
    }
}
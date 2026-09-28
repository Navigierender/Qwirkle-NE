package quirkle.game.gameplay.ui;

import quirkle.engine.AssetManager;
import quirkle.engine.EngineConfig;
import quirkle.engine.Entity;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class BoardFrame extends Entity {

    private BufferedImage scaledFrame;

    public BoardFrame() {
        this.renderOrder = UiTheme.LAYER_FRAME;
    }

    @Override
    public void onCreate() {
        scaledFrame = scaleToWindow(AssetManager.getTexture(UiTheme.FRAME_BOARD));
    }

    @Override
    public void onRender(Graphics2D g) {
        g.drawImage(scaledFrame, 0, 0, null);
    }

    private static BufferedImage scaleToWindow(BufferedImage source) {
        int width = EngineConfig.WINDOW_WIDTH;
        int height = EngineConfig.WINDOW_HEIGHT;

        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(source, 0, 0, width, height, null);
        g.dispose();

        return scaled;
    }
}

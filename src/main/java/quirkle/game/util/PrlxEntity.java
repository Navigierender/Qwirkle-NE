package quirkle.game.util;

import quirkle.engine.*;

import java.awt.Graphics2D;
import java.util.ArrayList;

public class PrlxEntity extends Entity {
    ArrayList<PrlxLayer> layer = new ArrayList<PrlxLayer>();

    int widthAtCreation;
    int heightAtCreation;
    int centerAtCreationX;
    int centerAtCreationY;

    public double tarX;
    public double tarY;

    static public class PrlxLayer {
        String textureIdentifier;
        double x;
        double y;
        double scale;
        double height;

        public PrlxLayer(String textureIdentifier, double height) {
            this.textureIdentifier = textureIdentifier;
            this.height = height;
        }
    }

    public PrlxEntity(PrlxLayer ... layers) {
        for (PrlxLayer l : layers) {
            layer.add(l);
        }
    }

    @Override
    public void onCreate() {
        widthAtCreation = SceneManager.getCurrentScene().getWidth();
        heightAtCreation = SceneManager.getCurrentScene().getHeight();
        centerAtCreationX = SceneManager.getCurrentScene().getCenterX();
        centerAtCreationY = SceneManager.getCurrentScene().getCenterY();

        tarX = centerAtCreationX;
        tarY = centerAtCreationY;

        for (PrlxLayer l : layer) {
            int textureWidth = AssetManager.getTexture(l.textureIdentifier).getWidth();
            l.scale = (textureWidth+2*(l.height*Math.abs(widthAtCreation-centerAtCreationX)))/(textureWidth);
        }
    }

    @Override
    public void onTick(double dt) {
        for (PrlxLayer l : layer) {
            l.x = centerAtCreationX+l.height*(tarX-centerAtCreationX);
            l.y = centerAtCreationY+l.height*(tarY-centerAtCreationY);
        }
    }

    @Override
    public void onRender(Graphics2D g) {
        for (PrlxLayer l : layer) {
            drawSprite(l.textureIdentifier, l.scale, l.x, l.y, 0.0, OriginPresets.CENTER, g);
        }
    }
}

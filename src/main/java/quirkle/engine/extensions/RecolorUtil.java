package quirkle.engine.extensions;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class RecolorUtil {

    public enum Colors {
        BLUE(new Color(65, 105, 225)),
        AQUA(new Color(0, 255, 255)),
        GREEN(new Color(34, 139, 34)),
        YELLOW(new Color(255, 215, 0)),
        PURPLE(new Color(128, 0, 128)),
        ORANGE(new Color(255, 140, 0));

        public final Color color;

        private Colors(Color color) {
            this.color = color;
        }
    }

    //idea source: https://gamedev.stackexchange.com/questions/163502/how-can-i-efficiently-change-the-color-of-a-bufferedimage
    public static BufferedImage recolor(BufferedImage source, Colors color) {
        BufferedImage texture = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

        Graphics2D g = texture.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.setComposite(AlphaComposite.SrcAtop);
        g.setColor(color.color);
        g.fillRect(0, 0, texture.getWidth(), texture.getHeight());
        g.dispose();

        return texture;
    }

    /**
     * Multiplies every pixel of {@code source} with {@code color}, so the sprite's bright parts
     * take on the color while its dark parts stay dark.
     */
    public static BufferedImage tint(BufferedImage source, Color color) {
        BufferedImage texture = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

        double red = color.getRed() / 255.0;
        double green = color.getGreen() / 255.0;
        double blue = color.getBlue() / 255.0;

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int argb = source.getRGB(x, y);

                int tintedRed = (int) (((argb >> 16) & 0xFF) * red);
                int tintedGreen = (int) (((argb >> 8) & 0xFF) * green);
                int tintedBlue = (int) ((argb & 0xFF) * blue);

                texture.setRGB(x, y, (argb & 0xFF000000) | (tintedRed << 16) | (tintedGreen << 8) | tintedBlue);
            }
        }

        return texture;
    }
}
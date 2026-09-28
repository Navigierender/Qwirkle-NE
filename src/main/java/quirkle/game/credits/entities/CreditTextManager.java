package quirkle.game.credits.entities;

import quirkle.engine.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class CreditTextManager extends Entity {

    ArrayList<CreditElement> creditElements = new ArrayList<CreditElement>();

    private static float titleFontSize = 50.0f;
    private static String titleFontIdentifier = "higher_jump";

    private static float contentFontSize = 50.0f;
    private static String contentFontIdentifier = "poly_regular";

    private static double dY = 1;
    private static double verticalSpacing;

    public double viewportHeight = 0;

    public class CreditElement {
        String title;
        String content;

        public CreditElement(String title, String content) {
            this.title = title;
            this.content = content;
        }

        public void render(Graphics2D g, double yCor) {
            double centerX = SceneManager.getCurrentScene().getCenterX();
            drawText(title, titleFontSize, Color.white, titleFontIdentifier, centerX, yCor, 0.0, OriginPresets.BOTTOM_MID, g);
            drawText(content, contentFontSize, Color.white, contentFontIdentifier, centerX, yCor, 0.0, OriginPresets.TOP_MID, g);
        }
    }

    /**
     * loads credit entrys from the lang files following the following scheme starting from 0:
     * creditEntry_title0; creditEntry_content0;
     * And fills up the CreditElement array
     */
    public CreditTextManager(int creditEntryCount) {
        for (int i = 0; i <= creditEntryCount; i++) {
            creditElements.add(
                new CreditElement(
                    AssetManager.getMessage("creditEntry_title" + i),
                    AssetManager.getMessage("creditEntry_content" + i)
                )
            );
        }
    }

    @Override
    public void onCreate() {
        verticalSpacing = SceneManager.getCurrentScene().getHeight() * 0.75;
        this.viewportHeight += creditElements.size() * verticalSpacing;

        this.viewportHeight += verticalSpacing ; //so everything is blended out at the end

        y += verticalSpacing;
    }

    @Override
    public void onTick(double dt) {
        if (y <= SceneManager.getCurrentScene().getCenterY()-viewportHeight) SceneManager.stopTempScene(); //maybe rework
        y -= dY;
    }

    @Override
    public void onRender(Graphics2D g) {
        int i = 0;
        for (CreditElement ce: creditElements) {
            ce.render(g, SceneManager.getCurrentScene().getCenterY()+i+y);
            i += verticalSpacing;
        }
    }
}

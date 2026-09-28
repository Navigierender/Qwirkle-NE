package quirkle.game.playerselect.scenes;

import quirkle.PersistentData;
import quirkle.engine.*;
import quirkle.game.gameplay.GamePlayScene;
import quirkle.game.gameplay.player.Player;
import quirkle.game.playerselect.entities.Arrow;
import quirkle.game.quickmenu.scenes.QuickMenuScene;
import quirkle.game.util.RectangularButton;
import quirkle.game.util.SimpleSpriteEntity;
import quirkle.game.util.SimpleTextEntity;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PlayerSelectScene extends Scene {

    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 4;

    Arrow upArrow;
    Arrow downArrow;

    private int playerCount = MIN_PLAYERS;
    private SimpleTextEntity playerCountBoard;

    private RectangularButton playButton;

    private static double arrowX;
    private static final int arrowSpacing = 300;

    @Override
    public void onCreate() {
        upArrow = new Arrow();
        downArrow = new Arrow();

        arrowX = getWidth() * 0.7;

        playerCountBoard = new SimpleTextEntity(String.valueOf(playerCount), 180.0f, "higher_jump", Color.WHITE, Entity.OriginPresets.CENTER);
        SimpleSpriteEntity boardBackdrop = new SimpleSpriteEntity("playerselect/label_backdrop", Entity.OriginPresets.CENTER);

        SimpleTextEntity title = new SimpleTextEntity(AssetManager.getMessage("playerselect_title"), 50.0f, "higher_jump", Color.BLACK, Entity.OriginPresets.CENTER_LEFT);
        SimpleTextEntity content = new SimpleTextEntity(AssetManager.getMessage("playerselect_content"), 50.0f, "poly_regular", Color.BLACK, Entity.OriginPresets.TOP_LEFT);

        playButton = new RectangularButton(AssetManager.getMessage("play"));

        addEntities(
                new SimpleSpriteEntity("playerselect/background", Entity.OriginPresets.TOP_LEFT),
                title, content, upArrow, downArrow, boardBackdrop, playerCountBoard, playButton
        );

        upArrow.rotation = 0.0;
        downArrow.rotation = 180.0;

        title.y = 150; title.x = 50;
        content.y = title.y + 150;  content.x = 50;

        boardBackdrop.y = getCenterY(); boardBackdrop.x = arrowX; boardBackdrop.scale = 1.4;
        playerCountBoard.y = getCenterY(); playerCountBoard.x = arrowX;

        upArrow.x = arrowX; downArrow.x = arrowX;
        upArrow.y = getCenterY() - arrowSpacing;
        downArrow.y = getCenterY() + arrowSpacing;

        playButton.x = arrowX;
        playButton.y = getHeight() - 100;
    }

    @Override
    public void onTick(double dt) {
        if (upArrow.isClicked()) {
            if (playerCount < MAX_PLAYERS) {
                playerCount++;
                playerCountBoard.setMessage(String.valueOf(playerCount));

                AssetManager.playSound("playerselect/increase",1.0);

            }   else AssetManager.playSound("problem",1.0);
        }

        if (downArrow.isClicked()) {
            if (playerCount > MIN_PLAYERS) {
                playerCount--;
                playerCountBoard.setMessage(String.valueOf(playerCount));

                AssetManager.playSound("playerselect/decrease",1.0);

            }   else AssetManager.playSound("problem",1.0);
        }

        if (playButton.isClicked() || InputManager.isKeyPressed(KeyEvent.VK_ENTER)) {
            SceneManager.setScene(new GamePlayScene(drawRandomPlayers(playerCount)));
        }

        if (InputManager.isKeyPressed(KeyEvent.VK_ESCAPE)) SceneManager.setTempScene(new QuickMenuScene(), true, false);
    }

    private static List<Player> drawRandomPlayers(int count) {
        List<String> namePool = new ArrayList<>(Arrays.asList(PersistentData.playerNames));
        Collections.shuffle(namePool);

        List<Player> players = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            players.add(new Player(namePool.remove(0)));
        }
        return players;
    }
}
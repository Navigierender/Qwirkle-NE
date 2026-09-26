package quirkle.game.gameplay;

import quirkle.engine.*;
import quirkle.game.debug.DebugMode;
import quirkle.game.endgame.scenes.EndGameScene;
import quirkle.game.gameplay.board.*;
import quirkle.game.gameplay.hand.*;
import quirkle.game.gameplay.player.Player;
import quirkle.game.gameplay.tiles.*;
import quirkle.game.gameplay.ui.*;
import quirkle.game.gameplay.ui.TurnScorePopup;
import quirkle.game.quickmenu.scenes.QuickMenuScene;
import quirkle.game.util.PlayRandomSound;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class GamePlayScene extends Scene {

    // Data
    private final Game game;
    private final BoardCamera camera;
    private final TileRack tileRack;

    // Zoom
    private static final double ZOOM_FACTOR = 1.0;

    // Drag
    private int lastMouseX;
    private int lastMouseY;
    private boolean isDragging = false;
    private boolean isDragBlocked = false;

    // Handover
    private final Handover handover = new Handover();
    private Player presentedPlayer;

    // UI
    private final BoardView boardView;
    private final BoardFrame boardFrame;
    private final TurnIndicator turnIndicator;
    private final PlayerCard playerCard;
    private final TileBagCounter tileBagCounter;
    private final TurnScorePopup turnScorePopup;

    public GamePlayScene(List<Player> players) {
        // the board is only visible inside the frame
        Rectangle viewport = UiTheme.boardViewport();

        this.game = new Game(new Board(), new TileBag(), players);
        this.camera = new BoardCamera(viewport.getCenterX(), viewport.getCenterY(), UiTheme.BOARD_BASE_CELL_SIZE);

        this.boardView = new BoardView(camera, viewport);
        this.boardFrame = new BoardFrame();
        this.tileRack = new TileRack((int) viewport.getCenterX(), UiTheme.rackCenterY(), handover);
        this.turnIndicator = new TurnIndicator(game);
        this.playerCard = new PlayerCard(game);
        this.tileBagCounter = new TileBagCounter(game.getTileBag());
        this.turnScorePopup = new TurnScorePopup(viewport);

        addEntities(boardView, boardFrame, turnIndicator, tileRack, playerCard, tileBagCounter, turnScorePopup);

        DebugMode.attach(this.game);
    }

    @Override
    public void onDestroy() {
        DebugMode.detach();
    }

    @Override
    public void onRender(Graphics2D g) {
        g.setColor(UiTheme.BACKGROUND);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void onTick(double dt) {
        // has to run first, the HUD uses it in its own tick
        handover.advance(dt);
        startHandoverIfTurnPassed();

        handleZoomInput();
        handleDragInput();
        handleClickInput();

        if (InputManager.isKeyPressed(KeyEvent.VK_ENTER)) endTurn();
        tileRack.showPlayer(game.getCurrentPlayer());

        updatePlacementPreview();

        boardView.sync(game.getBoard());

        if (InputManager.isKeyPressed(KeyEvent.VK_ESCAPE)) SceneManager.setTempScene(new QuickMenuScene(), true, true);
    }

    private void startHandoverIfTurnPassed() {
        Player currentPlayer = game.getCurrentPlayer();
        if (currentPlayer == presentedPlayer) return;
        if (handover.isRunning()) return;

        boolean isFirstFrame = presentedPlayer == null;
        presentedPlayer = currentPlayer;

        // no animation at the start of the game
        if (!isFirstFrame) handover.start();
    }

    /** Shows the selected tile on the hovered cell. Nothing is shown if the cell is already taken */
    private void updatePlacementPreview() {
        Tile selectedTile = tileRack.getSelectedTile();

        if (selectedTile == null || !isPointerOnBoard()) {
            boardView.setPlacementPreview(null, null);
            return;
        }

        Position hovered = camera.screenToBoard(InputManager.getMouseX(), InputManager.getMouseY());
        if (game.getBoard().isOccupied(hovered)) {
            boardView.setPlacementPreview(null, null);
            return;
        }

        boardView.setPlacementPreview(selectedTile, hovered);
    }

    private boolean isPointerOnBoard() {
        if (!boardView.contains(InputManager.getMouseX(), InputManager.getMouseY())) return false;

        return !tileRack.isHovered();
    }

    private void handleClickInput() {
        if (tileRack.handleInput()) return; // the rack got the click, don't also place a tile

        if (!InputManager.isMouseClicked()) return;
        // ignore clicks on the frame
        if (!boardView.contains(InputManager.getMouseX(), InputManager.getMouseY())) return;

        Position position = camera.screenToBoard(InputManager.getMouseX(), InputManager.getMouseY());

        // clicking a tile placed this turn takes it back
        if (game.getBoard().getPendingTiles().containsKey(position)) {
            game.takeBackTile(position);
            return;
        }

        Tile selectedTile = tileRack.getSelectedTile();
        if (selectedTile == null) return;

        try {
            game.placeTile(position, selectedTile);
            tileRack.clearSelection();
            PlayRandomSound.playRand("gameplay/placetile/0","gameplay/placetile/1","gameplay/placetile/2","gameplay/placetile/3");
        } catch (IllegalStateException e) {
            // Feld belegt oder Platzierung verstößt gegen die Qwirkle-Regeln -> Klick wird ignoriert
            tileRack.rejectSelection();
            EngineConfig.message(e.getMessage(), getClass().getSimpleName(), EngineConfig.messageType.INFO);
            AssetManager.playSound("problem", 1.0);
        }
    }

    private void handleZoomInput() {
        double scroll = InputManager.getScrollDelta();
        if (scroll == 0) return;
        if (!boardView.contains(InputManager.getMouseX(), InputManager.getMouseY())) return;

        camera.zoomBy(-scroll * ZOOM_FACTOR);
    }

    private void handleDragInput() {
        if (!InputManager.isRightMousePressed()) {
            isDragging = false;
            isDragBlocked = false;
            return;
        }

        if (isDragBlocked) return;

        if (isDragging) {
            camera.pan(InputManager.getMouseX() - lastMouseX, InputManager.getMouseY() - lastMouseY);
        } else if (!boardView.contains(InputManager.getMouseX(), InputManager.getMouseY())) {
            isDragBlocked = true;
            return;
        }

        lastMouseX = InputManager.getMouseX();
        lastMouseY = InputManager.getMouseY();
        isDragging = true;
    }

    private void endTurn() {
        try {
            int points = game.endTurn();
            tileRack.clearSelection();
            turnScorePopup.show(points);

            if (game.isOver()) {
                SceneManager.setScene(new EndGameScene(game));
                AssetManager.playSound("ingameplay scene endturn",1.0);
            }
        } catch (IllegalStateException e) {
            // Zug ist noch nicht abschließbar (z.B. kein Stein gelegt) -> Eingabe wird ignoriert
            EngineConfig.message(e.getMessage(), getClass().getSimpleName(), EngineConfig.messageType.INFO);
        }
    }
}

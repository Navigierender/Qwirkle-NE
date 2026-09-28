package quirkle.engine;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Arrays;

/*********************************************************************************************
 * InputManger is extended from MouseAdapter
 ********************************************************************************************/
public class InputManager extends MouseAdapter {
    private static final InputManager instance = new InputManager();
    private static final KeyHandler keyHandler = new KeyHandler();

    private static int mouseX;
    private static int mouseY;
    private static boolean mousePressed;
    private static boolean mouseClicked;
    private static boolean rightMousePressed;
    private static double scrollDelta;

    private static final boolean[] keys = new boolean[256];
    private static final boolean[] keysJustPressed = new boolean[256];

    private InputManager() {}

    public static InputManager getInstance() {
        return instance;
    }

    public static KeyHandler getKeyHandler() {
        return keyHandler;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        scrollDelta += e.getPreciseWheelRotation();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            mousePressed = true;
            mouseClicked = true;
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            rightMousePressed = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            mousePressed = false;
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            rightMousePressed = false;
        }
    }

    public static int getMouseX() { return mouseX; }
    public static int getMouseY() { return mouseY; }
    public static boolean isMousePressed() { return mousePressed; }
    public static boolean isMouseClicked() { return mouseClicked; }
    public static boolean isRightMousePressed() { return rightMousePressed; }
    public static double getScrollDelta() { return scrollDelta; }

    public static boolean isKeyDown(int keyCode) {
        return keyCode >= 0 && keyCode < keys.length && keys[keyCode];
    }

    public static boolean isKeyPressed(int keyCode) {
        return keyCode >= 0 && keyCode < keysJustPressed.length && keysJustPressed[keyCode];
    }

    public static void endFrame() {
        mouseClicked = false;
        scrollDelta = 0;
        Arrays.fill(keysJustPressed, false);
    }

    public static class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int code = e.getKeyCode();
            if (code >= 0 && code < keys.length) {
                if (!keys[code]) keysJustPressed[code] = true;
                keys[code] = true;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int code = e.getKeyCode();
            if (code >= 0 && code < keys.length) {
                keys[code] = false;
            }
        }
    }
}
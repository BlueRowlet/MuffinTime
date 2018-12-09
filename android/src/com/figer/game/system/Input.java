package com.figer.game.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Input implements InputProcessor {

    private Viewport viewport;
    private Vector2 inputVector;

    private boolean dragPossible;
    private boolean drag;
    private int dragX;
    private int dragY;

    private boolean tapSwitch;
    private boolean touchDownSwitch;
    private boolean touchUpSwitch;
    private boolean[] keyDownSwitch;
    private boolean[] keyUpSwitch;
    private int scrolledSwitch;

    public Input(Viewport viewport) {
        // Don't fiddle with this!
        this.viewport = viewport;
        inputVector = new Vector2();

        dragPossible = false;
        drag = false;
        dragX = 0;
        dragY = 0;

        tapSwitch = false;
        touchDownSwitch = false;
        touchUpSwitch = false;
        keyDownSwitch = new boolean[128];
        keyUpSwitch = new boolean[128];
        scrolledSwitch = 0;

        // VERY IMPORTANT!
        Gdx.input.setInputProcessor(this);
    }

    public int getX() {
        int x = (int) getCoordinates(Gdx.input.getX(), Gdx.input.getY(), viewport).x;
        x = Numeric.applyConstraintToInt(x, 0, Renderer.WIDTH);
        return x;
    }

    public int getY() {
        int y = (int) getCoordinates(Gdx.input.getX(), Gdx.input.getY(), viewport).y;
        y = Numeric.applyConstraintToInt(y, 0, Renderer.HEIGHT);
        return y;
    }

    public boolean isDrag() {
        return drag;
    }

    public int getDragDeltaX() {
        int dx = getDragX();
        int rx = getX();
        return rx - dx;
    }

    public int getDragDeltaY() {
        int dy = getDragY();
        int ry = getY();
        return ry - dy;
    }

    public boolean isTap() {
        return tapSwitch;
    }

    public boolean isKeyPressed(int keycode) {
        return Gdx.input.isKeyPressed(keycode);
    }

    public boolean isKeyDown(int keycode) {
        return keyDownSwitch[keycode];
    }

    public boolean isKeyUp(int keycode) {
        return keyUpSwitch[keycode];
    }

    public boolean isTouchDown() {
        return touchDownSwitch;
    }

    public boolean isTouched() {
        return Gdx.input.isTouched();
    }

    public boolean isTouchUp() {
        return touchUpSwitch;
    }

    public int scrollAmount() {
        return scrolledSwitch;
    }

    /**
     * Update must be called every frame.
     */
    public void update() {
        resetInputFlags();
        checkForDragging();
        updateDragging();
    }

    // *********************************************************************************************
    // * HELPERS
    // *********************************************************************************************

    private int getDragX() {
        int x = (int) getCoordinates(dragX, dragY, viewport).x;
        x = Numeric.applyConstraintToInt(x, 0, Renderer.WIDTH);
        return x;
    }

    private int getDragY(){
        int y = (int) getCoordinates(dragX, dragY, viewport).y;
        y = Numeric.applyConstraintToInt(y, 0, Renderer.HEIGHT);
        return y;
    }

    private void resetInputFlags() {
        tapSwitch = false;
        touchDownSwitch = false;
        touchUpSwitch = false;
        for (int i = 0; i < 128; i++) {
            keyDownSwitch[i] = false;
            keyUpSwitch[i] = false;
        }
        scrolledSwitch = 0;
    }

    private void checkForDragging() {
        if (!drag && dragPossible) {
            if (Math.abs(getDragDeltaX()) > 10 || Math.abs(getDragDeltaY()) > 10) {
                drag = true;
            }
        }
    }

    private void updateDragging() {
        if (drag) {
            dragX = Gdx.input.getX();
            dragY = Gdx.input.getY();
        }
    }

    private Vector2 getCoordinates(int x, int y, Viewport viewport) {
        inputVector.set(x, y);
        viewport.unproject(inputVector);
        return inputVector;
    }

    // *********************************************************************************************
    // * InputProcessor implementation
    // *********************************************************************************************

    @Override
    public boolean keyDown(int keycode) {
        if (keycode < 128) keyDownSwitch[keycode] = true;
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode < 128) keyUpSwitch[keycode] = true;
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchDownSwitch = true;
        dragPossible = true;
        drag = false;
        dragX = screenX;
        dragY = screenY;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        touchUpSwitch = true;
        if (!drag) tapSwitch = true;
        dragPossible = false;
        drag = false;
        dragX = 0;
        dragY = 0;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        scrolledSwitch = amount;
        return true;
    }
}

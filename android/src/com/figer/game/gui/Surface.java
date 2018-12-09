package com.figer.game.gui;

import com.badlogic.gdx.utils.Array;

import com.figer.game.system.Input;
import com.figer.game.system.Renderer;

public abstract class Surface {

    protected Array<Surface> children;
    protected boolean visible;

    protected int x;
    protected int y;
    protected int w;
    protected int h;
    protected int signal;

    public Surface(int x, int y, int w, int h) {
        children = new Array<Surface>();
        visible = true;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        signal = Signal.NULL;
    }

    public Surface (){
        children = new Array<Surface>();
        visible = false;
        signal = Signal.NULL;
    }

    public void draw(Renderer renderer) {
        if (visible) {
            drawBackground(renderer);
            drawContent(renderer);
            drawChildren(renderer);
            drawFrame(renderer);
        }
    }

    public abstract void drawContent(Renderer renderer);
    public abstract void update(Input input);

    void drawBackground(Renderer renderer) {
        renderer.drawRectangle(x, y, w, h, Renderer.DARK_GRAY);
    }

    void drawChildren(Renderer renderer) {
        for(Surface s : children) {
            s.draw(renderer);
        }
    }

    void drawFrame(Renderer renderer) {
        renderer.drawEmptyRectangle(x, y, w, h, Renderer.WHITE);
    }

    public void addChild(Surface newChild) {
        children.add(newChild);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean touchInside(Input input) {
        return (input.getX() >= x && input.getX() <= x+w &&
                input.getY() >= y && input.getY() <= y+h);
    }

    protected void setSignal(int signal) {
        this.signal = signal;
    }

    public int consumeSignal() {
        int temp = signal;
        signal = Signal.NULL;
        return temp;
    }

    public boolean isVisible() {
        return visible;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }
}

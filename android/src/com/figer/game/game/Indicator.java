package com.figer.game.game;

import com.figer.game.system.Renderer;

public class Indicator {
    public static final int W=32, H=32;

    private String name;
    private float x, y, scale;

    private boolean visible;

    public Indicator(String name, float x, float y){
        this.name = name;
        this.x = x;
        this.y = y;
        scale = 1;
        visible = true;
    }

    public void draw(Renderer renderer){
        if(visible){
            renderer.drawIndicator(x, y, name);
        }
    }

    //GETTERS AND SETTERS
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}

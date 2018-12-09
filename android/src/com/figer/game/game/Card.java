package com.figer.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.figer.game.system.Input;
import com.figer.game.system.Renderer;

import java.util.regex.Pattern;

public class Card {
    public static final int W = 96, H = 128;

    private String id;

    private String name;
    private String type;
    private String hp;
    private String dmg;
    private String effect;
    private String description;
    private float x, y, scale;

    private boolean visible;

    private int place;

    public Card(String id, float x, float y, int place){
        String filePath = id + ".txt";
        FileHandle cardFile = Gdx.files.internal(filePath);
        String data = cardFile.readString();

        String[] dataArray = data.split(Pattern.quote("$"));

        this.id = id;
        this.name = dataArray[0];
        this.type = dataArray[1];
        this.hp = dataArray[2];
        this.dmg = dataArray[3];
        this.effect = dataArray[4];
        this.description = dataArray[5];
        this.x = x;
        this.y = y;
        this.place = place;
        visible = true;
        scale = 1;
    }

    public Card(String id){
        this.id = id;
        scale = 1;
    }

    public void draw(Renderer renderer) { ;
        if(visible) {
            renderer.drawCard(x,y,id,scale);
        }
    }

    public void drawPreview(Renderer renderer){
        renderer.drawCard(500, 100, id, 3f);
    }

    public boolean touchInside(Input input){
        return (input.getX() > x && input.getX() < x + W &&
                input.getY() > y && input.getY() < y + H);
    }

    public String getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return scale;
    }

    public int getPlace() {
        return place;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHp() {
        return hp;
    }

    public void setHp(String hp) {
        this.hp = hp;
    }

    public String getDmg() {
        return dmg;
    }

    public void setDmg(String dmg) {
        this.dmg = dmg;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}

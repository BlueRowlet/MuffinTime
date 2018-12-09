package com.figer.game.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.figer.game.system.Input;
import com.figer.game.system.Renderer;

import java.util.Comparator;

public class CardCollection {
    private Array<Card> cards;
    private boolean hovered;

    private boolean visible;

    private int chosenCard = -1;

    public CardCollection() {
        cards = new Array<Card>();
        visible = true;
    }

    public void draw(Renderer renderer){
        if(visible) {
            for (Card c : cards) {
                c.draw(renderer);
                if (hovered) {
                    c.drawPreview(renderer);
                }
            }
        }
    }

    public void update(Input input){
        hovered = false;
        for(int i=cards.size-1; i>=0; i--){
            Card c = cards.get(i);
            if(c.touchInside(input) && !hovered){
                chosenCard = c.getPlace();
                c.setScale(1.2f);
                hovered = true;
            } else {
                c.setScale(1f);
            }
        }
    }

    public void sort(){
        cards.sort(new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {
                float c1 = o1.getX();
                float c2 = o2.getX();
                if (o1.getScale() > 1) c1 = Renderer.WIDTH;
                if (o2.getScale() > 1) c2 = Renderer.WIDTH;
                return Float.compare(c1, c2);
            }
        });
    }

    public Card addCard(String name, float x, float y, int place){
        Card card = new Card(name, x, y, place);
        cards.add(card);
        return card;
    }

    public Card getCard(int index){
        return cards.get(index);
    }

    public void removeCard(int i){
        cards.removeIndex(i);
    }

    public void getList(){
        for(int i=0; i<cards.size; i++){
            System.out.println(cards.get(i));
        }
    }

    public int randomCardPicker(){
        return MathUtils.random(0, cards.size - 1);
    }

    public int getSize(){
        return cards.size;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public int getChosenCard() {
        return chosenCard;
    }

    public void setChosenCard(int chosenCard) {
        this.chosenCard = chosenCard;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}

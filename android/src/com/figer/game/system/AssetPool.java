package com.figer.game.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

public class AssetPool {
    //Textures
    Texture cards;
    Texture indicators;

    //Texture Regions
    private ObjectMap<String, TextureRegion> cardRegion;
    private ObjectMap<String, TextureRegion> indicatorsRegion;

    public AssetPool(){
        cards = new Texture(Gdx.files.internal("cardsSheet.png"));
        indicators = new Texture(Gdx.files.internal("indicators.png"));

        cardRegion = new ObjectMap<>();
        cardRegion.put("cardBack", new TextureRegion(cards, 0,0,96,128));
        cardRegion.put("sadPepe", new TextureRegion(cards, 0,0,96,128));
        cardRegion.put("newChallenger", new TextureRegion(cards, 96,0,96,128));
        cardRegion.put("skrattarDu", new TextureRegion(cards, 192,0,96,128));
        cardRegion.put("imGay", new TextureRegion(cards, 288,0,96,128));
        cardRegion.put("pewdiepie", new TextureRegion(cards, 384, 0, 96, 128));
        cardRegion.put("markZuckerberg", new TextureRegion(cards, 480, 0, 96, 128));
        cardRegion.put("loss", new TextureRegion(cards,576,0,96,128));
        cardRegion.put("article13", new TextureRegion(cards, 672, 0, 96, 128));
        cardRegion.put("twitchThot", new TextureRegion(cards, 768, 0,96,128));
        cardRegion.put("ugandanKnuckles", new TextureRegion(cards, 864,0,96,128));

        indicatorsRegion = new ObjectMap<>();
        indicatorsRegion.put("lampGreen", new TextureRegion(indicators,0,0,32,32));
        indicatorsRegion.put("lampRed", new TextureRegion(indicators,32,0,32,32));
        for (TextureRegion region : cardRegion.values()) {
            region.flip(false, true);
        }
        for(TextureRegion region : indicatorsRegion.values()){
            region.flip(false, true);
        }
    }

    public void dispose(){
        cards.dispose();
    }

    public TextureRegion getCardRegion(String name) {
        return cardRegion.get(name);
    }
    public TextureRegion getIndicatorRegion(String name){
        return indicatorsRegion.get(name);
    }

}

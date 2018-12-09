package com.figer.game.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Renderer {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public static final int WHITE = 0;
    public static final int BLACK = 1;
    public static final int RED = 2;
    public static final int GREEN = 3;
    public static final int BLUE = 4;
    public static final int DARK_GRAY = 5;
    public static final int OLIVE = 6;

    private SpriteBatch batch;

    private Color[] colors;

    private OrthographicCamera camera;
    private Viewport viewport;

    private Texture systemTexture;
    private TextureRegion filler;
    private AssetPool assetPool;

    private BitmapFont defaultFont;
    private StringBuilder stringBuilder;
    private GlyphLayout glyphLayout;

    public Renderer() {
        batch = new SpriteBatch();

        colors = new Color[] {
                Color.WHITE,
                Color.BLACK,
                Color.RED,
                Color.GREEN,
                Color.BLUE,
                Color.DARK_GRAY,
                Color.OLIVE
        };

        camera = new OrthographicCamera(WIDTH, HEIGHT);
        camera.setToOrtho(true);
        camera.update();
        viewport = new FitViewport(WIDTH, HEIGHT, camera);
        viewport.apply(true);

        systemTexture = new Texture(Gdx.files.internal("gui.png"));
        filler = new TextureRegion(systemTexture);
        filler.flip(false, true);

        defaultFont = new BitmapFont(true);
        defaultFont.getRegion()
                .getTexture()
                .setFilter(
                        Texture.TextureFilter.Linear,
                        Texture.TextureFilter.Linear);

        stringBuilder = new StringBuilder();
        glyphLayout = new GlyphLayout();
        assetPool = new AssetPool();
    }

    public void begin() {
        batch.begin();
    }

    public void end() {
        batch.end();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
        viewport.apply();
    }

    public void prepare() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        clearToBlack();
    }

    // *********************************************************************************************
    // DRAWING
    // *********************************************************************************************

    public void drawRectangle(float x, float y, float w, float h, int colorCode) {
        drawRectangle(x, y, w, h, colorCode, 1f);
    }

    public void drawRectangle(float x, float y, float w, float h, int colorCode, float opacity) {
        Color c = colors[colorCode];
        batch.setColor(c.r, c.g, c.b, opacity);
        batch.draw(filler, x, y, w, h);
        batch.setColor(colors[WHITE]);
    }

    public void drawEmptyRectangle(float x, float y, float w, float h, int colorCode) {
        batch.setColor(colors[colorCode]);
        batch.draw(filler, x, y, w, 1);
        batch.draw(filler, x, y, 1, h);
        batch.draw(filler, x+w, y, 1, h);
        batch.draw(filler, x, y+h, w + 1, 1);
        batch.setColor(colors[WHITE]);
    }

    public void drawTextureRegion(TextureRegion region, float x, float y) {
        float w = region.getRegionWidth();
        float h = region.getRegionHeight();
        batch.draw(region, x, y, w, h);
    }

    public void drawTextureRegionScaled(TextureRegion region, float x, float y, float scale) {
        float w = region.getRegionWidth() * scale;
        float h = region.getRegionHeight() * scale;
        batch.draw(region, x, y, w, h);
    }

    public void drawInt(int value, float x, float y, int colorCode) {
        clearStringBuilder();
        stringBuilder.append(value);
        drawStringBuilder(x, y, colorCode);
    }

    public void drawText(String text, float x, float y, int colorCode) {
        clearStringBuilder();
        stringBuilder.append(text);
        drawStringBuilder(x, y, colorCode);
    }

    public void drawTextWithInt(String text, int intValue, float x, float y, int colorCode) {
        clearStringBuilder();
        stringBuilder.append(text);
        stringBuilder.append(": ");
        stringBuilder.append(intValue);
        drawStringBuilder(x, y, colorCode);
    }

    public void drawTextWithLong(String text, long longValue, float x, float y, int colorCode) {
        clearStringBuilder();
        stringBuilder.append(text);
        stringBuilder.append(": ");
        stringBuilder.append(longValue);
        drawStringBuilder(x, y, colorCode);
    }

    public void drawTextSlashed(String text, int value1, int value2, float x, float y, int colorCode) {
        clearStringBuilder();
        stringBuilder.append(text);
        stringBuilder.append(value1);
        stringBuilder.append("/");
        stringBuilder.append(value2);
        drawStringBuilder(x, y, colorCode);
    }

    public void drawTextCentered(String text, float x, float y, float w, float h,
                                 float offset, int colorCode) {
        glyphLayout.setText(defaultFont, text);
        float width = glyphLayout.width;
        float height = glyphLayout.height;
        float boundsCenterX = x + w / 2;
        float boundsCenterY = y + h / 2;
        float drawX = boundsCenterX - width / 2;
        float drawY = boundsCenterY - height / 2;
        drawText(text, drawX + offset, drawY + offset, colorCode);
    }

    // *********************************************************************************************
    // * CUSTOM SHIT
    // *********************************************************************************************

    public void drawCard(float x, float y, String name, float scale) {
        this.drawTextureRegionScaled(assetPool.getCardRegion(name), x, y, scale);
    }

    public void drawIndicator(float x, float y, String name){
        this.drawTextureRegion(assetPool.getIndicatorRegion(name),x,y);
    }

    // *********************************************************************************************
    // * HELPERS
    // *********************************************************************************************

    private void clearStringBuilder() {
        stringBuilder.setLength(0);
    }

    private void drawStringBuilder(float x, float y, int colorCode) {
        defaultFont.setColor(colors[colorCode]);
        defaultFont.draw(batch, stringBuilder, x, y);
        defaultFont.setColor(colors[WHITE]);
    }

    private void clearToBlack() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void dispose() {
        systemTexture.dispose();
        assetPool.dispose();
    }
}

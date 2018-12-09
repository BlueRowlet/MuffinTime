package com.figer.game.gui;

import com.figer.game.system.Input;
import com.figer.game.system.Renderer;

public class Button extends Surface {

    private String text;
    private int pushedOffset;

    public Button(int x, int y, int w, int h, String text) {
        super(x, y, w, h);
        this.text = text;
        this.pushedOffset = 0;
    }

    @Override
    public void update(Input input) {
        if (input.isTouchDown() && touchInside(input)) {
            pushedOffset = 1;
        } else if (input.isTouchUp()) {
            if (touchInside(input)) {
                setSignal(Signal.FIRE);
            }
            pushedOffset = 0;
        }
    }

    @Override
    public void drawContent(Renderer renderer) {
        renderer.drawTextCentered(text, x, y, w, h, pushedOffset, Renderer.WHITE);
    }
}

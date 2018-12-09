package com.figer.game.gui;

import com.badlogic.gdx.utils.Array;

import com.figer.game.system.Input;
import com.figer.game.system.Renderer;

public class List extends Surface {

    private int elementSize;
    private Array<String> elements;
    private int selectedIndex;

    public List(int x, int y, int w, int elementSize) {
        super(x, y, w, 0);
        this.elementSize = elementSize;
        elements = new Array<String>();
        selectedIndex = -1;
    }

    @Override
    public void update(Input input) {
        if (touchInside(input)) {
            selectedIndex = (input.getY() - y) / elementSize;
            if (input.isTap()) {
                setSignal(Signal.FIRE);
            }
        } else {
            selectedIndex = -1;
        }
    }

    @Override
    public void drawContent(Renderer renderer) {
        for (int i = 0; i < elements.size; i++) {

            if (i == selectedIndex)
                renderer.drawRectangle(x, y+i*elementSize, w, elementSize, Renderer.BLUE);

            renderer.drawText(elements.get(i), x + 5, y + 5 + i*elementSize, Renderer.WHITE);
            renderer.drawEmptyRectangle(x, y+i*elementSize, w, elementSize, Renderer.WHITE);
        }
    }

    @Override
    public void drawBackground(Renderer renderer) {
        renderer.drawRectangle(x, y, w, h, Renderer.OLIVE);
    }

    public void addElement(String element) {
        elements.add(element);
        resize();
    }

    public String getSelectedElement() {
        return elements.get(selectedIndex);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    private void resize() {
        h = elements.size * elementSize;
    }

}

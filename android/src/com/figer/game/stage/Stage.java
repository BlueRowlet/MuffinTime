package com.figer.game.stage;

import com.figer.game.system.Input;
import com.figer.game.system.Renderer;

public abstract class Stage {
    protected StageManager stageManager;

    public Stage(StageManager stageManager){
        this.stageManager = stageManager;
    }

    public abstract void draw(Renderer render);
    public abstract void update(Input input);
    public abstract void dispose();
    public abstract void onActivating();
    public abstract void onDeactivating();
}

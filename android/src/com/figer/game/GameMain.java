package com.figer.game;

import android.content.Context;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.figer.game.stage.Stage;
import com.figer.game.stage.StageManager;
import com.figer.game.system.Input;
import com.figer.game.system.Renderer;

public class GameMain extends ApplicationAdapter {
	Context context;
	private Renderer renderer;
	private Input input;
	private Array<Stage> stages;
	private StageManager stageManager;
	public GameMain(Context context){
		this.context = context;
	}

	@Override
	public void create () {
		renderer = new Renderer();
		input = new Input(renderer.getViewport());

		stages = new Array<Stage>();
		stageManager = new StageManager();
		stages.add(new GameStage(stageManager, context));
	}

	@Override
	public void render () {
		stageManager.update(stages);

		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// Drawing
		renderer.prepare();
		renderer.begin();
		stages.get(stageManager.getNumber()).draw(renderer);
		renderer.end();

		// Updating
		stages.get(stageManager.getNumber()).update(input);
		input.update();
	}
	
	@Override
	public void dispose () {
		renderer.dispose();
	}

	@Override
	public void resize(int w, int h) {
		renderer.resize(w, h);
	}
}

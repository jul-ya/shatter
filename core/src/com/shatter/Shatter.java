package com.shatter;

import com.shatter.system.BulletSystem;
import com.shatter.system.CollisionSystem;
import com.shatter.system.GunSystem;
import com.shatter.system.PhysicsSystem;
import com.shatter.system.RenderSystem;
import com.shatter.system.ShipSystem;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * This is the trial project (based on a simple vector-based asteroids game) for basic 2D voronoi shattering.
 * @author Ju Lia
 * @version 1.0
 * @since 2015-01-11
 */
public class Shatter extends ApplicationAdapter {
	Engine engine;
	Entity ship;
	RenderSystem reSys;
	World world;
	OrthographicCamera camera;
	Stage stage;

	/**
	 * This is the overwritten libgdx startup method.
	 * @see badlogic.gdx
	 */
	@Override
	public void create() {

		camera = new OrthographicCamera();
		reSys = new RenderSystem(camera);
		engine = new Engine();
		world = new World(engine);

		engine.addSystem(new PhysicsSystem(engine,world));
		engine.addSystem(new ShipSystem(world));
		engine.addSystem(reSys);
		engine.addSystem(new GunSystem(world));
		engine.addSystem(new BulletSystem());
		engine.addSystem(new CollisionSystem());

		ship = world.createShip();

		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);

		//spawning some asteroids at startup
		for(int i = 0; i<20; i++){
			if((int) (Math.random()*3)%2 != 0){
				world.createAsteroid(new Vector2(MathUtils.random(-50,50), MathUtils.random(50, 50)), 1f);
			} else {
				world.createAsteroid(new Vector2(MathUtils.random(50,50), MathUtils.random(-50, 50)), 2f);
			}
		}
	}

	/**
	 * This is the overwritten libgdx update method.
	 * @see badlogic.gdx
	 */
	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		this.camera.position.set(0.0F, 0.0F, 0.0F);

		this.camera.update();

		// update game logic
		engine.update(Gdx.graphics.getDeltaTime());
		MessageManager.getInstance().update(Gdx.graphics.getDeltaTime());
	}

	/**
	 * This is the overwritten libgdx resize method.
	 * @see badlogic.gdx
	 * @param width The width of the application window.
	 * @param height The height of the application window.
	 */
	@Override
	public void resize(int width, int height) {
		float ar = width / height;
		this.camera.setToOrtho(false, 50, 50 / ar);
		stage.getViewport().update(width, height, true);
	}

}

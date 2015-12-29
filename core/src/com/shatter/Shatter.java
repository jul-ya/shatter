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
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

/**
 * This is the trial project (based on a simple vector-based asteroids game) for
 * basic 2D voronoi shattering.
 * 
 * @author Ju Lia
 * @version 1.0
 * @since 2015-01-11
 */
public class Shatter extends ApplicationAdapter {
	Engine engine;
	Entity ship;
	RenderSystem reSys;
	TheWorld world;
	OrthographicCamera camera;

	/*World pworld;
	Box2DDebugRenderer debugRenderer;
	private float TIME_STEP = 1 / 60f;
	private float accumulator = 0;*/

	/**
	 * This is the overwritten libgdx startup method.
	 * 
	 * @see badlogic.gdx
	 */
	@Override
	public void create() {

		//pworld = new World(new Vector2(0, 0), true);
		//debugRenderer = new Box2DDebugRenderer();

		camera = new OrthographicCamera();
		reSys = new RenderSystem(camera);
		engine = new Engine();
		world = new TheWorld(engine/*, pworld*/);

		engine.addSystem(new PhysicsSystem(engine, world));
		engine.addSystem(new ShipSystem(world));
		engine.addSystem(reSys);
		engine.addSystem(new GunSystem(world));
		engine.addSystem(new BulletSystem());
		engine.addSystem(new CollisionSystem());

		ship = world.createShip();

		// spawning some asteroids at startup
		for (int i = 0; i < 10; i++) {
			if ((int) (Math.random() * 3) % 2 != 0) {
				world.createAsteroid(new Vector2(MathUtils.random(-50, 50), MathUtils.random(50, 50)), 1.5f);
			} else {
				world.createAsteroid(new Vector2(MathUtils.random(50, 50), MathUtils.random(-50, 50)), 2f);
			}
		}
	}

	/**
	 * This is the overwritten libgdx update method.
	 * 
	 * @see badlogic.gdx
	 */
	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glLineWidth(1.5f);

		this.camera.position.set(0.0F, 0.0F, 0.0F);

		this.camera.update();

		// update game logic
		engine.update(Gdx.graphics.getDeltaTime());
		MessageManager.getInstance().update(Gdx.graphics.getDeltaTime());

		// update box2d physics
		// debugRenderer.render(pworld, camera.combined);
		// updatePhysics(Gdx.graphics.getDeltaTime());
	}

	/**
	 * This is the overwritten libgdx resize method.
	 * 
	 * @see badlogic.gdx
	 * @param width
	 *            The width of the application window.
	 * @param height
	 *            The height of the application window.
	 */
	@Override
	public void resize(int width, int height) {
		float ar = width / height;
		this.camera.setToOrtho(false, 50, 50 / ar);
	}

	/*
	 * This method updates the box2d physics in a fixed time step with max
	 * frame time for slow devices.
	 * 
	 * @param deltaTime
	 *            The current deltaTime.
	 *
	private void updatePhysics(float deltaTime) {
		float frameTime = Math.min(deltaTime, 0.25f);
		accumulator += frameTime;
		while (accumulator >= TIME_STEP) {
			pworld.step(TIME_STEP, 6, 2);
			accumulator -= TIME_STEP;
		}
	}*/

}

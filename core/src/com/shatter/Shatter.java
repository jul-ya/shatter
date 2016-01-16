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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * This is the trial project (based on a simple vector-based asteroids game) for
 * basic dynamic 2D voronoi shattering.
 * 
 * @author Julia Angerer
 * @version 1.0
 */
public class Shatter extends ApplicationAdapter {
	Engine engine;
	Entity ship;
	RenderSystem reSys;
	CollisionSystem collSys;
	World world;
	OrthographicCamera camera;
	Label points;
	Label fps;
	Stage stage;

	/**
	 * This is the overwritten libgdx startup method.
	 * 
	 * @see badlogic.gdx
	 */
	@Override
	public void create() {

		camera = new OrthographicCamera();
		reSys = new RenderSystem(camera);
		engine = new Engine();
		world = new World(engine);
		collSys = new CollisionSystem(engine, world);

		engine.addSystem(collSys);
		engine.addSystem(new ShipSystem(world));
		engine.addSystem(reSys);
		engine.addSystem(new GunSystem(world));
		engine.addSystem(new BulletSystem());
		engine.addSystem(new PhysicsSystem());

		ship = world.createShip();

		// spawning some asteroids at startup
		for (int i = 0; i < 10; i++) {
			if ((int) (Math.random() * 3) % 2 != 0) {
				world.createAsteroid(new Vector2(MathUtils.random(-50, 50), MathUtils.random(50, 50)), 1.5f);
			} else {
				world.createAsteroid(new Vector2(MathUtils.random(50, 50), MathUtils.random(-50, 50)), 2f);
			}
		}

		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		LabelStyle textStyle;
		BitmapFont font = new BitmapFont();

		textStyle = new LabelStyle();
		textStyle.font = font;

		points = new Label("dynamic points: ", textStyle);
		points.setFontScale(1f, 1f);
		points.setBounds(10, 10, 10, 10);
		points.setColor(Color.RED);
		stage.addActor(points);
		
		fps = new Label("fps: ", textStyle);
		fps.setFontScale(1f, 1f);
		fps.setBounds(10, 22, 22, 22);
		fps.setColor(Color.ORANGE);
		stage.addActor(fps);
	}

	/**
	 * This is the overwritten libgdx update method.
	 * 
	 * @see badlogic.gdx
	 */
	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glLineWidth(1.5f);

		this.camera.position.set(0.0F, 0.0F, 0.0F);

		this.camera.update();
		
		if(Gdx.input.isKeyPressed(Input.Keys.A)){
			collSys.incrPointCount();
		} else if(Gdx.input.isKeyPressed(Input.Keys.D)){
			collSys.decrPointCount();
		}

		// update game logic
		engine.update(Gdx.graphics.getDeltaTime());
		MessageManager.getInstance().update(Gdx.graphics.getDeltaTime());
		
		fps.setText("fps: " + Gdx.graphics.getFramesPerSecond());
		points.setText("dynamic points: " + collSys.getPointCount());

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
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
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

}

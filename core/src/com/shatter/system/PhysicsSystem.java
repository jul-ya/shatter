package com.shatter.system;

import com.shatter.TheWorld;
import com.shatter.component.Bullet;
import com.shatter.component.Collider;
import com.shatter.component.Fracture;
import com.shatter.component.Movement;
import com.shatter.component.Physics;
import com.shatter.component.Position;
import com.shatter.component.Ship;

import java.util.concurrent.TimeUnit;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class PhysicsSystem extends IteratingSystem {
	private ComponentMapper<Movement> mm = ComponentMapper.getFor(Movement.class);
	private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
	// private ComponentMapper<Physics> phm =
	// ComponentMapper.getFor(Physics.class);
	protected Engine engine;
	private TheWorld world;

	public PhysicsSystem(Engine engine, TheWorld world) {
		super(Family.all(Movement.class, Position.class).get());
		this.engine = engine;
		this.world = world;
		MessageManager.getInstance().addListener(new Telegraph() {

			@Override
			public boolean handleMessage(Telegram msg) {
				if (msg == null) {
					return false;
				}
				Entity e = (Entity) msg.extraInfo;
				if (e.getComponent(Bullet.class) != null || e.getComponent(Ship.class) != null) {
					removeE(e);
				} else { // when entity is asteroid
					createP(e);
					removeE(e);
				}
				return true;
			}

		}, 1);
	}

	public void removeE(Entity e) {
		engine.removeEntity(e);
	}

	public void createP(Entity e) {
		for (int i = 0; i < 100; i++) {
			world.createParticle(e.getComponent(Position.class).pos.x, e.getComponent(Position.class).pos.y,
					(float) (Math.random() * 360), (float) (Math.random() * 20));
		}
		if (e.getComponent(Collider.class).radius >= 2f) {

			Fracture fract = e.getComponent(Fracture.class);

			// dynamic update test and time measuring
			long startTime = System.nanoTime();
			fract.triangulator.dynamicUpdatePoints(new Vector2[]{new Vector2(0, 0), new Vector2(0.5f, 0.5f)});
			//fract.triangulator.dynamicUpdatePoint(new Vector2(0, 0));
			long stopTime = System.nanoTime();
			System.out.println((stopTime - startTime) / 100000.0);

			// it was a big one with voronoi cells, spawn new fractures
			for (int i = 0; i < fract.triangulator.getVDiagram().size(); i++) {
				float[] vertices = fract.triangulator.getVDiagram().get(i);
				world.createAsteroidVD(
						new Vector2(e.getComponent(Position.class).pos.x, e.getComponent(Position.class).pos.y),
						vertices);
			}
		} else {
			// it was a small one without voronoi cells, maybe spawn new one
			if ((int) (Math.random() * 3) % 2 != 0) {
				world.createAsteroid(new Vector2(MathUtils.random(-50, 50), MathUtils.random(-50, 50)), 1.5f);
			}
		}
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Movement m = mm.get(entity);
		Position p = pm.get(entity);

		Vector2 damp = m.vel.cpy().scl(m.damp);
		p.pos.mulAdd(m.vel, deltaTime);
		m.vel.mulAdd((m.acc).sub(damp), deltaTime);
		p.angle += m.angVel * deltaTime;

		/*
		 * box2d physics Physics ph = phm.get(entity); if(ph != null){
		 * ph.body.setLinearDamping(m.damp);
		 * ph.body.setTransform(p.pos.mulAdd(m.vel, deltaTime), (float)
		 * Math.toRadians(p.angle));
		 * ph.body.setLinearVelocity(m.vel.mulAdd((m.acc).sub(damp),
		 * deltaTime)); }
		 */
	}
}

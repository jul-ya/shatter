package com.shatter.system;

import com.shatter.World;
import com.shatter.component.Bullet;
import com.shatter.component.Collider;
import com.shatter.component.Movement;
import com.shatter.component.Position;
import com.shatter.component.Ship;
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

public class PhysicsSystem extends IteratingSystem{
	private ComponentMapper<Movement> mm = ComponentMapper.getFor(Movement.class);
	private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
	protected Engine engine;
	private World world;

	public PhysicsSystem(Engine engine, World world) {
		super(Family.all(Movement.class, Position.class).get());
		this.engine = engine;
		this.world = world;
		MessageManager.getInstance().addListener(new Telegraph(){

			@Override
			public boolean handleMessage(Telegram msg) {
				if(msg == null){
					return false;
				}
				Entity e = (Entity) msg.extraInfo;
				if(e.getComponent(Bullet.class) != null || e.getComponent(Ship.class) != null){
					removeE(e);
				} else { //when entity is asteroid
					createP(e);
					removeE(e);
				}
				return true;
			}
			
		}, 1);
	}
	
	public void removeE(Entity e){
		engine.removeEntity(e);
	}
	
	public void createP(Entity e){
		for(int i = 0; i<100; i++){
			world.createParticle(e.getComponent(Position.class).pos.x, e.getComponent(Position.class).pos.y, (float) (Math.random()*360), (float) (Math.random()*20));
		}
		if(e.getComponent(Collider.class).radius >= 3f){
			//if it was a big one, spawn two new on the asteroids position
			world.createAsteroid(new Vector2(e.getComponent(Position.class).pos.x, e.getComponent(Position.class).pos.y), 1f);
			world.createAsteroid(new Vector2(e.getComponent(Position.class).pos.x, e.getComponent(Position.class).pos.y), 1.5f);
		} else {
			//if it was a small one, spawn new one randomly
			if((int) (Math.random()*3)%2 != 0){
				world.createAsteroid(new Vector2(MathUtils.random(-50,50), MathUtils.random(-50, 50)), 1f);
			} else {
				world.createAsteroid(new Vector2(MathUtils.random(-50,50), MathUtils.random(-50, 50)), 2f);
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
	}
}

package com.shatter.system;

import com.shatter.World;
import com.shatter.component.Movement;
import com.shatter.component.Position;
import com.shatter.component.Ship;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

public class ShipSystem extends IteratingSystem {

	private ComponentMapper<Movement> mm = ComponentMapper.getFor(Movement.class);
	private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
	private ComponentMapper<Ship> sm = ComponentMapper.getFor(Ship.class);
	private World world;

	public ShipSystem(World world) {
		super(Family.all(Movement.class, Ship.class, Position.class).get());
		this.world = world;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Movement m = mm.get(entity);
		Position p = pm.get(entity);
		Ship s = sm.get(entity);
		
		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			p.angle += s.ROT_SPEED;
		} else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			p.angle -= s.ROT_SPEED;
		}
		if (Gdx.input.isKeyPressed(Keys.UP)) {
			m.acc = new Vector2(0, 1).rotate(p.angle).scl(s.SPEED);
		} else {
			m.acc.x = 0;
			m.acc.y = 0;
		}
		
		if (m.acc.y != 0){
			world.createParticle(p.pos.x + (float)(Math.sin(Math.toRadians(p.angle))), p.pos.y - (float)(Math.cos(Math.toRadians(p.angle))), p.angle-180, (float) (Math.random()*30));
		}
	}

}

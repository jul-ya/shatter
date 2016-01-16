package com.shatter.system;

import com.shatter.component.Movement;
import com.shatter.component.Position;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

public class PhysicsSystem extends IteratingSystem {
	private ComponentMapper<Movement> mm = ComponentMapper.getFor(Movement.class);
	private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);

	public PhysicsSystem() {
		super(Family.all(Movement.class, Position.class).get());
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

package com.shatter.system;

import com.shatter.component.Bullet;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class BulletSystem extends IteratingSystem {
	private ComponentMapper<Bullet> bm = ComponentMapper.getFor(Bullet.class);
	private Engine engine;

	public BulletSystem() {
		super(Family.all(Bullet.class).get());
	}

	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		this.engine = engine;
	}

	protected void processEntity(Entity entity, float deltaTime) {
		Bullet bullet = bm.get(entity);
		bullet.lifetime -= deltaTime;
		if (bullet.lifetime <= 0.0F) {
			destroyBullet(entity);
		}
	}

	private void destroyBullet(Entity entity) {
		this.engine.removeEntity(entity);
	}
}

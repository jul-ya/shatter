package com.shatter.system;

import com.shatter.World;
import com.shatter.component.Gun;
import com.shatter.component.Position;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

public class GunSystem extends IteratingSystem {
	private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
	private ComponentMapper<Gun> gm = ComponentMapper.getFor(Gun.class);
	private World world;
	private Vector2 bulletVector = new Vector2();

	public GunSystem(World world) {
		super(Family.all(Gun.class, Position.class).get());
		this.world = world;
	}

	protected void processEntity(Entity entity, float deltaTime) {
		Position p = pm.get(entity);
		Gun g = gm.get(entity);
		if (g.coolTime > 0.0F) {
			g.coolTime -= deltaTime;
			if (g.coolTime > 0.0F) {
				return;
			}
		}
		if (!Gdx.input.isKeyPressed(Keys.SPACE)) {
			return;
		}
		this.bulletVector.set(g.offset).rotateRad(p.angle).add(p.pos);
		this.world.createBullet(bulletVector.x, bulletVector.y, p.angle);
		g.coolTime = g.coolDown;
	}

}

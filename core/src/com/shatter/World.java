package com.shatter;

import java.util.ArrayList;
import java.util.Random;

import com.shatter.component.Bullet;
import com.shatter.component.Collider;
import com.shatter.component.Fracture;
import com.shatter.component.Gun;
import com.shatter.component.Movement;
import com.shatter.component.Position;
import com.shatter.component.Ship;
import com.shatter.component.Visual;
import com.shatter.dt.Triangulator;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/**
 * This class represents the game world and is able to create certain entities.
 * 
 * @author Julia Angerer
 * @version 1.0
 */
public class World {

	private Engine engine;
	private Random rand = new Random();

	public World(Engine engine) {
		this.engine = engine;
	}

	/**
	 * This method creates the Entity called Ship.
	 * 
	 * @return Entity The ship entity.
	 */
	public Entity createShip() {
		Entity ship = new Entity();

		Movement m = new Movement();
		m.setDamp(1.0f);
		ship.add(m);
		
		Position p = new Position();
		ship.add(p);
		
		Visual v = new Visual();
		ship.add(v);
		ship.add(new Ship());

		Gun g = new Gun();
		g.setCoolDown(0.18f);
		g.setOffset(new Vector2(0.0f, 1.5f));
		ship.add(g);

		Collider c = new Collider();
		c.setRadius(1.0f);
		c.setFlag(1);
		c.setMask(2);
		ship.add(c);

		this.engine.addEntity(ship);
		return ship;
	}

	/**
	 * This method creates the Entity called Bullet.
	 * 
	 * @return Entity The bullet entity.
	 */
	public Entity createBullet(float x, float y, float angle) {
		float[] VERTICES = { 0.0F, 0.25F, -0.25F, -0.25F, 0.0F, -0.175F, 0.25F, -0.25F };
		Vector2 vel = new Vector2(0.0F, 30.0F).rotate(angle);
		Entity bullet = new Entity();

		Position p = new Position();
		p.setPos(new Vector2(x, y));
		p.setAngle(angle);
		bullet.add(p);

		Movement m = new Movement();
		m.setVel(vel);
		bullet.add(m);

		Visual v = new Visual();
		v.setVERTICES(VERTICES);
		v.setCOLOR(Color.ORANGE);
		bullet.add(v);

		bullet.add(new Bullet());

		Collider c = new Collider();
		c.setRadius(0.4f);
		c.setFlag(4);
		c.setMask(2);
		bullet.add(c);

		this.engine.addEntity(bullet);
		return bullet;
	}

	/**
	 * This method creates the Entity called Particle.
	 * 
	 * @return Entity The particle entity.
	 */
	public Entity createParticle(float x, float y, float angle, float lifetime) {
		float[] VERTICES = { 0.0F, 0.15F, -0.15F, -0.15F, 0.15F, -0.15F };
		Vector2 vel = new Vector2(0.0F, (float) (Math.random() * 30)).rotate(angle);
		Entity particle = new Entity();

		Position p = new Position();
		p.setPos(new Vector2(x, y));
		p.setAngle(angle);
		particle.add(p);

		Movement m = new Movement();
		m.setAngVel((float) (Math.random() * 360));
		m.setVel(vel);
		particle.add(m);

		Visual v = new Visual();
		v.setVERTICES(VERTICES);
		Color c = new Color();
		c.r = (float) (Math.random() * 255);
		c.g = (float) (Math.random() * 255);
		c.b = (float) (Math.random() * 255);
		v.setCOLOR(c);
		particle.add(v);

		Bullet b = new Bullet();
		b.setLifeTime(lifetime / 80);
		particle.add(b);

		this.engine.addEntity(particle);
		return particle;
	}

	/**
	 * This method creates the Entity called Asteroid.
	 * 
	 * @return Entity The asteroid entity.
	 */
	public Entity createAsteroid(Vector2 pos, float size) {
		Entity asteroid = new Entity();

		Position p = new Position();
		p.setPos(pos);
		asteroid.add(p);

		Vector2 vel = new Vector2(this.rand.nextFloat() * 4f, 0f);
		vel.rotate(360.0F * this.rand.nextFloat()); // random velocity rotated
													// randomly

		Movement m = new Movement();
		m.setVel(vel);
		m.setAngVel(this.rand.nextFloat() * 2f);
		asteroid.add(m);

		float[] vertices = new float[10 * 2]; // creating vertices with different
												// offset to midpoint
		Vector2 ps = new Vector2(); // including size variable in offset
		for (int i = 0; i < 10; i++) {
			float offset = size * this.rand.nextFloat()/size;

			ps.set(offset + size, 0f);
			ps.rotate(360 / 10 * i);

			vertices[(i * 2)] = ps.x;
			vertices[(i * 2 + 1)] = ps.y;
		}

		Visual v = new Visual();
		v.setVERTICES(vertices);
		v.setCOLOR(Color.BLACK);
		asteroid.add(v);

		// add fracture component
		Fracture f = new Fracture();
		ArrayList<Vector2> points = new ArrayList<Vector2>();
		for (int i = 0; i < vertices.length / 2; i++) {
			points.add(new Vector2(vertices[(i * 2)], vertices[(i * 2 + 1)]));
		}
		Triangulator d = new Triangulator(points);
		f.setTriangulator(d);
		
		System.out.println(d.getDTriangles().size());
		System.out.println(d.getVDiagram().size());
		asteroid.add(f);

		Collider c = new Collider();
		c.setRadius(1.5f * size);
		c.setFlag(2);
		c.setMask(5);
		asteroid.add(c);

		this.engine.addEntity(asteroid);
		return asteroid;
	}

	/**
	 * This method creates the Entity called AsteroidVD.
	 * 
	 * @return Entity The asteroid entity.
	 */
	public Entity createAsteroidVD(Vector2 pos, float[] vertices) {
		Entity asteroid = new Entity();

		Position p = new Position();
		p.setPos(pos);
		asteroid.add(p);

		Vector2 vel = new Vector2(this.rand.nextFloat() * 4f, 0f);
		vel.rotate(360.0F * this.rand.nextFloat()); // random velocity rotated
													// randomly

		Movement m = new Movement();
		m.setVel(vel);
		m.setAngVel(this.rand.nextFloat() * 2f);
		asteroid.add(m);

		Visual v = new Visual();
		v.setVERTICES(vertices);
		v.setCOLOR(Color.BLACK);
		asteroid.add(v);

		Collider c = new Collider();
		c.setRadius(1f);
		c.setFlag(2);
		c.setMask(5);
		asteroid.add(c);

		this.engine.addEntity(asteroid);
		return asteroid;
	}
}

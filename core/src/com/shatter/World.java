package com.shatter;

import java.util.Random;

import com.shatter.component.Bullet;
import com.shatter.component.Collider;
import com.shatter.component.Fracture;
import com.shatter.component.Gun;
import com.shatter.component.Movement;
import com.shatter.component.Position;
import com.shatter.component.Ship;
import com.shatter.component.Visual;
import com.shatter.dt.DT;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/**
 * This class represents the game world and is able to create certain entities.
 * @author Ju Lia
 */
public class World {

	private Engine engine;
	private Random rand = new Random();

	public World(Engine engine) {
		this.engine = engine;
	}

	/**
	 * This method creates the Entity called Ship.
	 * @return Entity The ship entity.
	 */
	public Entity createShip() {
		Entity ship = new Entity();

		Movement m = new Movement();
		m.setDamp(1.0f);
		
		ship.add(m);
		ship.add(new Position());
		ship.add(new Visual());
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
	 * @return Entity The particle entity.
	 */
	public Entity createParticle(float x, float y, float angle, float lifetime) {
		float[] VERTICES = { 0.0F, 0.15F, -0.15F, -0.15F, 0.15F, -0.15F };
		Vector2 vel = new Vector2(0.0F, (float) (Math.random()*30)).rotate(angle);
		Entity particle = new Entity();

		Position p = new Position();
		p.setPos(new Vector2(x, y));
		p.setAngle(angle);
		particle.add(p);

		Movement m = new Movement();
		m.setAngVel((float) (Math.random()*360));
		m.setVel(vel);
		particle.add(m);

		Visual v = new Visual();
		v.setVERTICES(VERTICES);
		Color c = new Color();
		c.r = (float) (Math.random()*255);
		c.g = (float) (Math.random()*255);
		c.b = (float) (Math.random()*255);
		v.setCOLOR(c);
		particle.add(v);

		Bullet b = new Bullet();
		b.setLifeTime(lifetime/80);
		particle.add(b);
		
		this.engine.addEntity(particle);
		return particle;
	}

	/**
	 * This method creates the Entity called Asteroid.
	 * @return Entity The asteroid entity.
	 */
	public Entity createAsteroid(Vector2 pos, float size) {
		Entity asteroid = new Entity();
		
		Position p = new Position();
		p.setPos(pos);
		asteroid.add(p);
		
		Vector2 vel = new Vector2(this.rand.nextFloat()* 4f, 0f);
		vel.rotate(360.0F * this.rand.nextFloat()); //random velocity rotated randomly

		Movement m = new Movement();
		m.setVel(vel);
		m.setAngVel(this.rand.nextFloat() * 2f);
		asteroid.add(m);

		float[] vertices = new float[10 * 2]; //creating vertices with different offset to midpoint
	    Vector2 ps = new Vector2();			 //including size variable in offset
		for (int i = 0; i < 10; i++) {
			float offset = size * this.rand.nextFloat();

			ps.set(offset + size, 0f);
			ps.rotate(360 / 10 * i);
			
			vertices[(i * 2)] = ps.x;
			vertices[(i * 2 + 1)] = ps.y;
		}
		/*float[] vertices;
		if(size == 2f){
			vertices = new float[]{-2.0f, 0.0f, -1.4f, 1.4f, 0f,2f,1.4f,1.4f,2f,0f, 1.4f,-1.4f,0f,-2f,-1.4f,-1.4f};
		} else {
			vertices = new float[]{-1.0f, 0.0f, -0.7f, 0.7f, 0f,1f,0.7f,0.7f,1f,0f, 0.7f,-0.7f,0f,-1f,-0.7f,-0.7f};
		}*/
	    
		Visual v = new Visual();
		v.setVERTICES(vertices);
		v.setCOLOR(Color.WHITE);
		asteroid.add(v);
		
		
		// add fracture component
		Fracture f = new Fracture();
		asteroid.add(f);

		/* determine min/max x/y
		float minX = vertices[0];
		float maxX = vertices[0];
		float minY = vertices[0];
		float maxY = vertices[0];
		for (int i = 1; i < vertices.length; i++) {
			if (i % 2 == 0) {
				if (vertices[i] < minX) {
					minX = vertices[i];
				}
				if (vertices[i] > maxX) {
					maxX = vertices[i];
				}
			} else {
				if (vertices[i] < minY) {
					minY = vertices[i];
				}
				if (vertices[i] > maxY) {
					maxY = vertices[i];
				}
			}
		}

		// generate points inside the asteroid and save them in the fracture component
		// including intersection test (is point inside Asteroid?) - jordan curve theorem
		int counter = 0;
		for (int k = 0; k < (f.pointList.length)/2; k++) {
			if(k<(f.pointList.length-vertices.length)/2){
			boolean isInside = false;
			while (!isInside) {
				f.pointList[(k * 2)] = MathUtils.random(minX, maxX);
				f.pointList[(k * 2 + 1)] = MathUtils.random(minY, maxY);

				int j = vertices.length / 2 - 1;
				for (int i = 0; i < vertices.length / 2; i++) {
					if (vertices[(i * 2)] < f.pointList[(k * 2)] && vertices[(j * 2)] >= f.pointList[(k * 2)]
							|| vertices[(j * 2)] < f.pointList[(k * 2)]
									&& vertices[(i * 2)] >= f.pointList[(k * 2)]) {
						if (vertices[(i * 2 + 1)] + (f.pointList[(k * 2)] - vertices[(i * 2)])
								/ (vertices[(j * 2)] - vertices[(i * 2)])
								* (vertices[(j * 2 + 1)] - vertices[(i * 2 + 1)]) < f.pointList[(k * 2 + 1)]) {
							isInside = !isInside;
						}
					}
					j = i;
				}
			}
			} else {
				f.pointList[(k * 2)] = vertices[(counter * 2)];
				f.pointList[(k * 2 + 1)] = vertices[(counter * 2 + 1)];
				counter++;
			}
		}*/
		
		Vector2[] points = new Vector2[vertices.length/2];
		for(int i = 0; i<vertices.length/2; i++) {
			points[i] = new Vector2(vertices[(i * 2)],vertices[(i * 2 + 1)]);
			System.out.println(points[i]);
		}
		DT d = new DT(points);
		f.dt = d.getDT();
		
		System.out.println(f.dt.size());
		
		
		Collider c = new Collider();
		c.setRadius(1.5f*size);
		c.setFlag(2);
		c.setMask(5);
		asteroid.add(c);

		this.engine.addEntity(asteroid);
		return asteroid;
	}
	
	/**
	 * This method creates the Entity called Asteroid.
	 * @return Entity The asteroid entity.
	 */
	public Entity createAsteroidDT(Vector2 pos, float[] vertices) {
		Entity asteroid = new Entity();
		
		Position p = new Position();
		p.setPos(pos);
		asteroid.add(p);
		
		Vector2 vel = new Vector2(this.rand.nextFloat()* 4f, 0f);
		vel.rotate(360.0F * this.rand.nextFloat()); //random velocity rotated randomly

		Movement m = new Movement();
		m.setVel(vel);
		m.setAngVel(this.rand.nextFloat() * 2f);
		asteroid.add(m);
	    
		Visual v = new Visual();
		v.setVERTICES(vertices);
		v.setCOLOR(Color.WHITE);
		asteroid.add(v);
		
		
		// add fracture component
		Fracture f = new Fracture();
		asteroid.add(f);
		
		Vector2[] points = new Vector2[vertices.length/2];
		for(int i = 0; i<vertices.length/2; i++) {
			points[i] = new Vector2(vertices[(i * 2)],vertices[(i * 2 + 1)]);
			System.out.println(points[i]);
		}
		DT d = new DT(points);
		f.dt = d.getDT();
		
		System.out.println(f.dt.size());
		
		
		Collider c = new Collider();
		c.setRadius(1f);
		c.setFlag(2);
		c.setMask(5);
		asteroid.add(c);

		this.engine.addEntity(asteroid);
		return asteroid;
	}
}

package com.shatter.system;

import com.shatter.World;
import com.shatter.component.Bullet;
import com.shatter.component.Collider;
import com.shatter.component.Fracture;
import com.shatter.component.Movement;
import com.shatter.component.Position;
import com.shatter.component.Ship;
import java.util.Random;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

/**
 * This class handles and detects collision as well as performs the dynamic
 * shatter of asteroids with fracture components.
 * 
 * @author Julia Angerer
 * @version 1.0
 */
public class CollisionSystem extends EntitySystem {
	private ImmutableArray<Entity> entities;
	private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
	private ComponentMapper<Collider> cm = ComponentMapper.getFor(Collider.class);

	private Engine engine;
	private World world;
	private int pointCount = 10;
	private FileHandle logFile = Gdx.files.local("log.ser");

	/**
	 * The constructor of this entitysystem.
	 * 
	 * @param engine
	 *            The engine of the game.
	 * @param world
	 *            The world of the game.
	 */
	public CollisionSystem(Engine engine, World world) {
		this.engine = engine;
		this.world = world;
	}

	/**
	 * Gets the entities when added to engine.
	 */
	public void addedToEngine(Engine engine) {
		this.entities = engine.getEntitiesFor(Family.all(Collider.class, Position.class).get());
	}

	/**
	 * Sets the entities to null when removed from engine.
	 */
	public void removedFromEngine(Engine engine) {
		this.entities = null;
	}

	/**
	 * The update method of the entity system.
	 */
	public void update(float deltaTime) {
		for (int i = 0; i < this.entities.size(); i++) {
			Entity e1 = (Entity) this.entities.get(i);
			for (int j = i + 1; j < this.entities.size(); j++) {
				Entity e2 = (Entity) this.entities.get(j);

				if ((e1.getComponent(Collider.class).flag & e2.getComponent(Collider.class).mask) != 0
						&& (e2.getComponent(Collider.class).flag & e1.getComponent(Collider.class).mask) != 0) {
					if (collided(e1, e2)) {
						// actual collision with objects
						onCollided(new Entity[] { e1, e2 });
					}
				}
			}
		}
	}

	/**
	 * This method detects collision between entities.
	 * 
	 * @param a
	 *            The entity a.
	 * @param b
	 *            The entity b.
	 * @return boolean the result of the detection
	 */
	private boolean collided(Entity a, Entity b) {
		Position p1 = (Position) this.pm.get(a);
		Collider c1 = (Collider) this.cm.get(a);

		Position p2 = (Position) this.pm.get(b);
		Collider c2 = (Collider) this.cm.get(b);

		double distance = p1.pos.dst2(p2.pos);
		double r = c1.radius * c1.radius + c2.radius * c2.radius;

		if (distance <= r) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method does collision handling.
	 * 
	 * @param entities
	 *            The two collided entities.
	 */
	private void onCollided(Entity[] entities) {
		Entity ship = null;
		Entity bullet = null;
		Entity asteroid = null;
		Vector2 bulletDirection = null;

		for (Entity e : entities) {
			if (e.getComponent(Bullet.class) != null)
				bullet = e;
			else if (e.getComponent(Ship.class) != null)
				ship = e;
			else
				asteroid = e;
		}

		if (ship != null) {
			engine.removeEntity(ship);
		}
		if (bullet != null) {
			bulletDirection = bullet.getComponent(Movement.class).vel.nor();
			engine.removeEntity(bullet);
		}
		if (asteroid != null) {
			if (bulletDirection != null) {
				shatterAsteroid(asteroid, bulletDirection);
			}
			createParticles(asteroid);
			engine.removeEntity(asteroid);
		}
	}

	/**
	 * This method shatters an asteroid and dynamically adds fracture points in
	 * the given sector
	 * 
	 * @param asteroid
	 *            The asteroid entity.
	 * @param bulletDirection
	 *            The bullets direction.
	 */
	public void shatterAsteroid(Entity asteroid, Vector2 bulletDirection) {
		if (asteroid.getComponent(Fracture.class) != null) {

			Fracture fract = asteroid.getComponent(Fracture.class);

			// sector fracturing
			Vector2[] p = new Vector2[pointCount];
			Random rand = new Random();
			for (int i = 0; i < p.length; i++) {
				float x = rand.nextFloat() * 1.2f;
				float y = rand.nextFloat() * 1.2f;
				if (bulletDirection.x > 0) {
					x = -x; // invert bulletDirection x
				}
				if (bulletDirection.y > 0) {
					y = -y; // invert bulletDirection y
				}
				p[i] = new Vector2(x, y);
			}

			// dynamic update test and time measuring
			long startTime = System.nanoTime();
			fract.triangulator.dynamicUpdatePoints(p);
			long stopTime = System.nanoTime();
			logFile.writeString("" + ((stopTime - startTime) / 1000000.0), true);
			logFile.writeString("\n", true);

			// it was a big one with voronoi cells, spawn new fractures and
			// remove them within 5 seconds
			for (int i = 0; i < fract.triangulator.getVDiagram().size(); i++) {
				float[] vertices = fract.triangulator.getVDiagram().get(i);
				if (vertices.length > 2) { // just to be sure
					Entity e = world.createAsteroidVD(new Vector2(asteroid.getComponent(Position.class).pos.x,
							asteroid.getComponent(Position.class).pos.y), vertices);
					Timer.schedule(new Task() {
						@Override
						public void run() {
							engine.removeEntity(e);
						}
					}, (float) (Math.random() * 5.0f));
				}
			}

			// spawn new asteroid because one was destroyed
			if ((int) (Math.random() * 3) % 2 != 0) {
				world.createAsteroid(new Vector2(MathUtils.random(-50, 50), MathUtils.random(-50, 50)), 1.5f);
			} else {
				world.createAsteroid(new Vector2(MathUtils.random(-50, 50), MathUtils.random(-50, 50)), 2f);
			}
		}
	}

	/**
	 * This method creates particles for an entity.
	 * 
	 * @param e
	 *            The entity.
	 */
	public void createParticles(Entity e) {
		for (int i = 0; i < 100; i++) {
			world.createParticle(e.getComponent(Position.class).pos.x, e.getComponent(Position.class).pos.y,
					(float) (Math.random() * 360), (float) (Math.random() * 20));
		}
	}

	/**
	 * Getter for the dynamic point count.
	 * 
	 * @return int the point count
	 */
	public int getPointCount() {
		return pointCount;
	}

	/**
	 * Increments the dynamic point count.
	 */
	public void incrPointCount() {
		if (this.pointCount < 1000) {
			this.pointCount++;
		}
	}

	/**
	 * Decrements the dynamic point count.
	 */
	public void decrPointCount() {
		if (this.pointCount > 1) {
			this.pointCount--;
		}
	}

}

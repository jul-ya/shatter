package com.shatter.system;

import com.shatter.component.Collider;
import com.shatter.component.Position;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ai.msg.MessageManager;

public class CollisionSystem extends EntitySystem {
	private ImmutableArray<Entity> entities;
	private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
	private ComponentMapper<Collider> cm = ComponentMapper.getFor(Collider.class);

	public void addedToEngine(Engine engine) {
		this.entities = engine.getEntitiesFor(Family.all(Collider.class, Position.class).get());
	}

	public void removedFromEngine(Engine engine) {
		this.entities = null;
	}

	public void update(float deltaTime) {
		for (int i = 0; i < this.entities.size(); i++) {
			Entity e1 = (Entity) this.entities.get(i);
			for (int j = i + 1; j < this.entities.size(); j++) {
				Entity e2 = (Entity) this.entities.get(j);
				
				if((e1.getComponent(Collider.class).flag & e2.getComponent(Collider.class).mask)!=0 && (e2.getComponent(Collider.class).flag & e1.getComponent(Collider.class).mask)!=0){
					if (processEntity(e1, e2)) {
						//actual collision with objects
						MessageManager.getInstance().dispatchMessage(0, null, null, 1, e1);
						MessageManager.getInstance().dispatchMessage(0, null, null, 1, e2);
					}
				}
			}
		}
	}

	private boolean processEntity(Entity a, Entity b) {
		Position p1 = (Position) this.pm.get(a);
		Collider c1 = (Collider) this.cm.get(a);

		Position p2 = (Position) this.pm.get(b);
		Collider c2 = (Collider) this.cm.get(b);

		double distance = p1.pos.dst2(p2.pos);
		double r = c1.radius * c1.radius + c2.radius * c2.radius;
		
		if(distance <= r){
			return true;
		} else {
			return false;
		}
	}

}

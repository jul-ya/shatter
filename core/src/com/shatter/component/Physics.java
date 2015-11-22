package com.shatter.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Physics implements Component{
	public Body body;
	
	public void setBody(Vector2 pos, World pworld, float[] vertices){
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(pos);

		body = pworld.createBody(bodyDef);

		PolygonShape poly = new PolygonShape();
		poly.set(vertices);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = poly;
		fixtureDef.density = 1.0f;
		fixtureDef.restitution = 0.5f;

		body.createFixture(fixtureDef);

		poly.dispose();
	}
}

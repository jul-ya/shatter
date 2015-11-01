package com.shatter.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class Position implements Component{
	public Vector2 pos = new Vector2();
	public float angle = 0.0f;
	
	public void setPos(Vector2 pos) {
		this.pos = pos;
	}
	public void setAngle(float angle) {
		this.angle = angle;
	}
}

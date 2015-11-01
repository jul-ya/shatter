package com.shatter.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class Movement implements Component {
	public Vector2 vel = new Vector2();
	public float angVel = 0.0f;
	public Vector2 acc = new Vector2();
	public float damp = 0.0f;
	
	public void setVel(Vector2 vel) {
		this.vel = vel;
	}
	public void setAngVel(float angVel) {
		this.angVel = angVel;
	}
	public void setAcc(Vector2 acc) {
		this.acc = acc;
	}
	public void setDamp(float damp) {
		this.damp = damp;
	}
}

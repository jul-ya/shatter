package com.shatter.component;

import com.badlogic.ashley.core.Component;

public class Ship implements Component {
	public float SPEED = 15.0f;
	public float ROT_SPEED = 5.0f;
	
	public void setSPEED(float s) {
		this.SPEED = s;
	}
	public void setROT_SPEED(float rs) {
		this.ROT_SPEED = rs;
	}
}
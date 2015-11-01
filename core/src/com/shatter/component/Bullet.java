package com.shatter.component;

import com.badlogic.ashley.core.Component;

public class Bullet implements Component {
	public float lifetime = 1.0F;

	public void setLifeTime(float timeToLive) {
		this.lifetime = timeToLive;
	}
}

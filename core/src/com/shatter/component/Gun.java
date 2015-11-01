package com.shatter.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class Gun implements Component {
	public float coolDown = 1.0F;
	public float coolTime;
	public Vector2 offset = new Vector2();
	
	public void setCoolDown(float coolDown) {
		this.coolDown = coolDown;
	}
	public void setCoolTime(float timeToCool) {
		this.coolTime = timeToCool;
	}
	public void setOffset(Vector2 offset) {
		this.offset = offset;
	}
}

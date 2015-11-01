package com.shatter.component;

import com.badlogic.ashley.core.Component;

public class Collider implements Component{
	public float radius = 1.0f;
	public int flag;
	public int mask;

	public void setRadius(float radius) {
		this.radius = radius;
	}
	
	public void setFlag(int flag){
		this.flag = flag;
	}
	
	public void setMask(int mask){
		this.mask = mask;
	}
}

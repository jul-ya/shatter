package com.shatter.component;


import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

public class Visual implements Component{
	public float[] VERTICES = { 0.0f, 1.0f, -1.0f, -1.0f, 0.0f,-0.7f, 1.0f, -1.0f };
	public Color COLOR = Color.ORANGE;
	
	public void setVERTICES(float[] v) {
		this.VERTICES = v;
	}
	public void setCOLOR(Color c) {
		this.COLOR = c;
	}
	
}

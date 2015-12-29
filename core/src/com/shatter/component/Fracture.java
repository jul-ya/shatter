package com.shatter.component;

import com.badlogic.ashley.core.Component;
import com.shatter.dt.DT;

public class Fracture implements Component{
	public DT triangulator;

	public void setTriangulator(DT triangulator) {
		this.triangulator = triangulator;
	}
}

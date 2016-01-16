package com.shatter.component;

import com.badlogic.ashley.core.Component;
import com.shatter.dt.Triangulator;

public class Fracture implements Component {
	public Triangulator triangulator;

	public void setTriangulator(Triangulator triangulator) {
		this.triangulator = triangulator;
	}
}

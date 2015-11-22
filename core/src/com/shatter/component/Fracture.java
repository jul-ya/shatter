package com.shatter.component;

import java.util.ArrayList;
import com.badlogic.ashley.core.Component;
import com.shatter.dt.Triangle;

public class Fracture implements Component{
	//public float[] pointList = new float[12*2]; //7 vertices, 5 random points
	public ArrayList<Triangle> dt;
	public ArrayList<float[]> vd;
}

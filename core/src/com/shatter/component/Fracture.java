package com.shatter.component;

import com.badlogic.ashley.core.Component;
import com.shatter.dt.Triangulator;

/**
 * This class represents the fracture component and stores a triangulator
 * object.
 * 
 * @author Julia Angerer
 * @version 1.0
 */
public class Fracture implements Component {
	public Triangulator triangulator;

	/**
	 * Setter for the triangulation object.
	 * 
	 * @param triangulator
	 *            The triangulation object.
	 */
	public void setTriangulator(Triangulator triangulator) {
		this.triangulator = triangulator;
	}
}

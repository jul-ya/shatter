package com.shatter.dt;

import com.badlogic.gdx.math.Vector2;

/**
 * This class represents an edge in the triangulation. It has different
 * methods which are used in the triangulation algorithm.
 * 
 * @author Julia Angerer
 * @version 1.0
 */
public class Edge {

	/**
	 * The edge point a.
	 */
	private Vector2 a;
	
	/**
	 * The edge point b.
	 */
	private Vector2 b;

	/**
	 * The constructor for the edge.
	 * 
	 * @param a
	 *            Vector2 of A
	 * @param b
	 *            Vector2 of B
	 */
	public Edge(Vector2 a, Vector2 b) {
		this.a = a;
		this.b = b;
	}

	/**
	 * This method compares two edges to determine if they are equal/duplicates.
	 * 
	 * @param e
	 *            Edge the edge to compare
	 * @return boolean The result of the comparison.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Edge)) {
			return false;
		}
		Edge e = (Edge) obj;
		return a.equals(e.a) && b.equals(e.b) || a.equals(e.b) && b.equals(e.a);
	}

	/**
	 * This method overrides the hashcode method.
	 * 
	 * @return int The new hashcode.
	 */
	@Override
	public int hashCode() {
		final int hashMult = 31;
		int hashSum = a.hashCode() + b.hashCode();
		return hashMult * hashSum;
	}

	/**
	 * Getter of point A.
	 * 
	 * @return Vector2 a
	 */
	public Vector2 getA() {
		return a;
	}

	/**
	 * Getter of point B.
	 * 
	 * @return Vector2 b
	 */
	public Vector2 getB() {
		return b;
	}

}

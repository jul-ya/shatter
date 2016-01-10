package com.shatter.dt;

import com.badlogic.gdx.math.Vector2;

public class Edge {

	protected Vector2 a;
	protected Vector2 b;

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
	 * Setter of point A.
	 * 
	 * @param a
	 *            Vector2 of A
	 */
	public void setA(Vector2 a) {
		this.a = a;
	}

	/**
	 * Getter of point B.
	 * 
	 * @return Vector2 b
	 */
	public Vector2 getB() {
		return b;
	}

	/**
	 * Setter of point B.
	 * 
	 * @param b
	 *            Vector2 of B
	 */
	public void setB(Vector2 b) {
		this.b = b;
	}

}

package com.shatter.dt;

import com.badlogic.gdx.math.Vector2;

public class Triangle {

	protected Vector2 a;
	protected Vector2 b;
	protected Vector2 c;

	protected float ccRadius;
	protected Vector2 ccCenter;

	private static double EPSILON = 1.0e-6;

	/**
	 * The constructor for the triangle, calculates the circumcircle already.
	 * 
	 * @param a
	 *            Vector2 of A
	 * @param b
	 *            Vector2 of B
	 * @param c
	 *            Vector2 of C
	 */
	public Triangle(Vector2 a, Vector2 b, Vector2 c) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.calcCircumCircle();
	}

	/**
	 * This method calculates the CircumCircle of the Triangle.
	 * 
	 * reference used: https://en.wikipedia.org/wiki/Circumscribed_circle
	 */
	public void calcCircumCircle() {
		float A = this.b.x - this.a.x;
		float B = this.b.y - this.a.y;
		float C = this.c.x - this.a.x;
		float D = this.c.y - this.a.y;

		float E = A * (this.a.x + this.b.x) + B * (this.a.y + this.b.y);
		float F = C * (this.a.x + this.c.x) + D * (this.a.y + this.c.y);

		float G = 2.0f * (A * (this.c.y - this.b.y) - B * (this.c.x - this.b.x));

		float dx, dy;

		if (Math.abs(G) < EPSILON) {
			// this triangle is collinear - just find min+max values and use the
			// midpoint
			float minx = Math.min(this.a.x, this.b.x);
			minx = Math.min(minx, this.c.x);
			float miny = Math.min(this.a.y, this.b.y);
			miny = Math.min(miny, this.c.y);
			float maxx = Math.max(this.a.x, this.b.x);
			maxx = Math.min(maxx, this.c.x);
			float maxy = Math.max(this.a.y, this.b.y);
			maxy = Math.min(maxy, this.c.y);

			ccCenter = new Vector2((minx + maxx) / 2, (miny + maxy) / 2);
			dx = ccCenter.x - minx;
			dy = ccCenter.y - miny;

		} else {

			float cx = (D * E - B * F) / G;
			float cy = (A * F - C * E) / G;

			ccCenter = new Vector2(cx, cy);
			dx = ccCenter.x - this.a.x;
			dy = ccCenter.y - this.a.y;
		}

		float radius_squared = dx * dx + dy * dy;
		this.ccRadius = (float) Math.sqrt(radius_squared);
	}

	/**
	 * This method checks if a point is in the Circumcirlce of the Triangle.
	 * 
	 * @param v
	 *            Vector2 the point to check
	 * @return boolean The result of the check.
	 */
	public boolean inCC(Vector2 v) {
		float dx = this.ccCenter.x - v.x;
		float dy = this.ccCenter.y - v.y;
		float dist_squared = dx * dx + dy * dy;

		return (dist_squared <= Math.pow(this.ccRadius, 2));
	};

	/**
	 * This method determines if this triangle contains a point shared with
	 * another Triangle.
	 * 
	 * @param t
	 *            The other triangle.
	 * @return boolean The result of the comparison.
	 */
	public boolean containsPoint(Triangle t) {
		return a.equals(t.a) || a.equals(t.b) || a.equals(t.c) || b.equals(t.a) || b.equals(t.b) || b.equals(t.c)
				|| c.equals(t.a) || c.equals(t.b) || c.equals(t.c);
	}

	/**
	 * This method determines if this triangle contains a given point.
	 * 
	 * @param p
	 *            The point given.
	 * @return boolean The result of the comparison.
	 */
	public boolean containsPoint(Vector2 p) {
		return a.equals(p) || b.equals(p) || c.equals(p);
	}

	/**
	 * This method converts a triangle into a, for the libGDX
	 * ShapeRenderer.polygon method suitable, vertex array.
	 * 
	 * @return float[] The Triangle as an vertex array.
	 */
	public float[] toVertexArray() {
		return new float[] { a.x, a.y, b.x, b.y, c.x, c.y };
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

	/**
	 * Getter of point C.
	 * 
	 * @return Vector2 c
	 */
	public Vector2 getC() {
		return c;
	}

	/**
	 * Setter of point C.
	 * 
	 * @param c
	 *            Vector2 of C
	 */
	public void setC(Vector2 c) {
		this.c = c;
	}

	/**
	 * Getter of the cc radius.
	 * 
	 * @return float ccRadius
	 */
	public float getCcRadius() {
		return ccRadius;
	}

	/**
	 * Getter of the cc center.
	 * 
	 * @return Vector2 ccCenter
	 */
	public Vector2 getCcCenter() {
		return ccCenter;
	}

}

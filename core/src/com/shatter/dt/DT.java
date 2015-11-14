package com.shatter.dt;

import java.util.ArrayList;
import com.badlogic.gdx.math.Vector2;

/**
 * This class constructs a delaunay triangulation of a given mesh, which is the
 * dual graph of a voronoi diagram. The algorithm used is called
 * BowyerWatson-Algorithm and has a complexity from O(n log n) to O(n^2) in
 * special cases. As it is an incremental approach, new points can simply be
 * generated and added to the triangulation.
 * 
 * references used constructing the main algorithm implementation:
 * https://takisword.wordpress.com/2009/08/13/bowyerwatson-algorithm/
 * https://en.wikipedia.org/wiki/Bowyer%E2%80%93Watson_algorithm
 * http://www.cs.cornell.edu/info/people/chew/delaunay.html
 * http://paulbourke.net/papers/triangulate/
 * 
 * @author Ju Lia
 * @version 1.0
 * @since 2015-14-11
 */
public class DT {

	Vector2[] points;
	ArrayList<Triangle> dTriangles;

	/**
	 * The constructor for the DT.
	 * 
	 * @param points
	 *            The set of points given.
	 */
	public DT(Vector2[] points) {
		this.points = points;
	}

	/**
	 * This method creates the corresponding SuperTriangle of the given amount
	 * of points.
	 * 
	 * @param points
	 *            The given points.
	 * @return Triangle The SuperTriangle that contains all the points.
	 */
	public static Triangle getSuperTriangle(Vector2[] points) {

		// calculate xmin, xmax, ymin, ymax = the outer points of the given set
		float xMin = points[0].x;
		float yMin = points[0].y;
		float xMax = points[0].x;
		float yMax = points[0].y;

		for (int i = 0; i < points.length; i++) {
			if (points[i].x < xMin) {
				xMin = points[i].x;
			}
			if (points[i].x > xMax) {
				xMax = points[i].x;
			}
			if (points[i].y < yMin) {
				yMin = points[i].y;
			}
			if (points[i].y > yMax) {
				yMax = points[i].y;
			}
		}

		float distanceX = xMax - xMin;
		float distanceY = yMax - yMin;
		float distanceMax = Math.max(distanceX, distanceY);

		float xMiddle = (xMin + xMax) / 2f;
		float yMiddle = (yMin + yMax) / 2f;

		// create triangle large enough to contain all points - doesn't have to
		// be the bounding triangle (=smallest one possible)
		Triangle superTriangle = new Triangle(new Vector2(xMiddle - 2f * distanceMax, yMiddle - distanceMax),
				new Vector2(xMiddle, yMiddle + 2f * distanceMax),
				new Vector2(xMiddle + 2f * distanceMax, yMiddle - distanceMax));

		return superTriangle;
	}

	/**
	 * The real DT calculation using Bowyer-Watson incremental algorithm happens
	 * here.
	 * 
	 * @return ArrayList<Triangles> The triangulated Triangle list.
	 */
	public ArrayList<Triangle> getDT() {

		// triangle buffer, containing current valid triangles
		ArrayList<Triangle> triangleBuffer = new ArrayList<Triangle>();

		// add the superTriangle to the buffer
		Triangle superT = getSuperTriangle(points);
		triangleBuffer.add(superT);

		// for each point in the point set
		for (Vector2 vertex : points) {
			// TODO: O(n^2) - optimize by sorting vertices along the x-axis and
			// then only circumcircle check triangles that are on the right
			triangleBuffer = addPoint(vertex, triangleBuffer);
		}

		// if triangle contains a vertex from supertriangle, remove triangle
		for (int i = triangleBuffer.size() - 1; i >= 0; i--) {
			if (triangleBuffer.get(i).containsPoint(superT)) {
				triangleBuffer.remove(i);
			}
		}

		// final delaunay triangle set
		dTriangles = triangleBuffer;
		return triangleBuffer;
	}

	/**
	 * This method adds one Point at a time to the Triangulation.
	 * 
	 * @param point
	 *            The Point to add.
	 * @param triangles
	 *            The Triangle list that will be updated.
	 * @return ArrayList<Triangle> the new valid triangles
	 */
	public ArrayList<Triangle> addPoint(Vector2 point, ArrayList<Triangle> triangles) {

		// edge buffer, contains the edges that will be re-added to new
		// triangles
		ArrayList<Edge> edgeBuffer = new ArrayList<Edge>();

		// iterate over all triangles (in reverse order because the size of the
		// list is changed but not checked anymore)
		for (int j = triangles.size() - 1; j >= 0; j--) {

			Triangle T = triangles.get(j);

			// if point is inside a triangles circumcircle, add edges to buffer
			// and remove triangle
			if (T.inCC(point)) {
				edgeBuffer.add(new Edge(T.getA(), T.getB()));
				edgeBuffer.add(new Edge(T.getB(), T.getC()));
				edgeBuffer.add(new Edge(T.getC(), T.getA()));
				triangles.remove(j);
			}

		}

		// remove the duplicate edges and keep only the unique ones
		edgeBuffer = removeDuplicateEdges(edgeBuffer);

		// now build new triangles from edgebuffer edges
		for (Edge e : edgeBuffer) {
			triangles.add(new Triangle(e.a, e.b, point));
		}

		return triangles;
	}

	/**
	 * This method removes duplicate edges from an ArrayList.
	 * 
	 * @param edges
	 *            The ArrayList to check.
	 * @return ArrayList<Edge> the remaining unique edges
	 */
	public ArrayList<Edge> removeDuplicateEdges(ArrayList<Edge> edges) {

		// TODO: optimize from O(n^2) to O(n) with hash?
		ArrayList<Edge> newEdges = new ArrayList<Edge>();

		// iterate over all edges
		for (int i = 0; i < edges.size(); ++i) {
			Edge edge1 = edges.get(i);
			boolean isUnique = true;
			for (int j = 0; j < edges.size(); ++j) {
				if (i == j) {
					continue;
				}
				Edge edge2 = edges.get(j);
				if (edge1.isDuplicate(edge2) || edge1.inverse().isDuplicate(edge2)) {
					isUnique = false;
					break;
				}
			}

			// only save the unique ones to the new set
			if (isUnique) {
				newEdges.add(edge1);
			}
		}

		return newEdges;
	}

}

package com.shatter.dt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;

import com.badlogic.gdx.math.ConvexHull;
import com.badlogic.gdx.math.Vector2;

/**
 * This class constructs a delaunay triangulation of a given mesh, which is the
 * dual graph of a voronoi diagram. The algorithm used is called
 * BowyerWatson-Algorithm and has a runtime complexity of O(n^2). As it is an
 * incremental approach, new points can simply be generated and added to the
 * triangulation. A voronoi diagram can be generated out of this triangulation.
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

	/**
	 * All points to be triangulated, unsorted but in ccw order.
	 */
	private ArrayList<Vector2> points;

	/**
	 * All points to be triangulated, sorted by x values.
	 */
	private ArrayList<Vector2> sortedPoints;

	/**
	 * The dynamically added points that form in the game.
	 */
	private ArrayList<Vector2> dynamicPoints = new ArrayList<Vector2>();

	/**
	 * The supertriangle for the given point set.
	 */
	private Triangle superT;

	/**
	 * The list of all triangles including the supertriangle.
	 */
	private ArrayList<Triangle> dTrianglesAll;

	/**
	 * The list of all triangles that share a point with the polygon outline,
	 * excluding all the supertriangle triangles.
	 */
	private ArrayList<Triangle> dTriangles;

	/**
	 * The list of Voronoi diagram cells.
	 */
	private ArrayList<float[]> vDiagram = new ArrayList<float[]>();

	public ArrayList<Triangle> getDTriangles() {
		return dTriangles;
	}

	public ArrayList<float[]> getVDiagram() {
		return vDiagram;
	}

	/**
	 * The constructor for the DT.
	 * 
	 * @param points
	 *            The set of points given.
	 */
	@SuppressWarnings("unchecked")
	public DT(ArrayList<Vector2> points) {
		this.points = points;

		// pre-sort the points on x-axis
		this.sortedPoints = (ArrayList<Vector2>) points.clone();
		Collections.sort(sortedPoints, new Comparator<Vector2>() {

			@Override
			public int compare(Vector2 arg0, Vector2 arg1) {
				if (arg0.x < arg1.x) {
					return -1;
				} else if (arg0.x == arg1.x) {
					return 0;
				} else {
					return 1;
				}
			}

		});

		getDT();
		getVD();
	}

	/**
	 * This method gets the extreme points (bounding rectangle) of the given
	 * point set.
	 * 
	 * @param points
	 *            The given points.
	 * @return float[] the extremes in order: xMin, yMin, xMax, yMax
	 */
	private float[] getMinMax(ArrayList<Vector2> points) {

		// calculate xmin, xmax, ymin, ymax = the outer points of the given set
		float xMin = points.get(0).x;
		float yMin = points.get(0).y;
		float xMax = points.get(0).x;
		float yMax = points.get(0).y;

		for (int i = 0; i < points.size(); i++) {
			if (points.get(i).x < xMin) {
				xMin = points.get(i).x;
			}
			if (points.get(i).x > xMax) {
				xMax = points.get(i).x;
			}
			if (points.get(i).y < yMin) {
				yMin = points.get(i).y;
			}
			if (points.get(i).y > yMax) {
				yMax = points.get(i).y;
			}
		}

		return new float[] { xMin, yMin, xMax, yMax };
	}

	/**
	 * This method creates the corresponding SuperTriangle of the given amount
	 * of points.
	 * 
	 * @param points
	 *            The given points.
	 * @return Triangle the SuperTriangle that contains all the points
	 */
	private Triangle getSuperTriangle(ArrayList<Vector2> points) {

		float[] extremes = getMinMax(points);
		float xMin = extremes[0];
		float yMin = extremes[1];
		float xMax = extremes[2];
		float yMax = extremes[3];

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
	 */
	@SuppressWarnings("unchecked")
	private void getDT() {

		// triangle buffer, containing current valid triangles
		dTriangles = new ArrayList<Triangle>();

		// add the superTriangle to the buffer
		superT = getSuperTriangle(sortedPoints);
		dTriangles.add(superT);

		// for each point in the point set
		for (Vector2 vertex : sortedPoints) {
			// would be O(n^2) - optimized by sorting points along the x-axis
			// and only cc check the triangles on the right = now O(n^1.5)
			dTriangles = addPoint(vertex, dTriangles);
		}

		// save DT including superTriangle for computing the VD
		dTrianglesAll = (ArrayList<Triangle>) dTriangles.clone();

		// for displaying and clipping reasons:
		// if triangle contains a vertex from superTriangle, remove triangle
		for (int i = dTriangles.size() - 1; i >= 0; i--) {
			if (dTriangles.get(i).containsPoint(superT)) {
				dTriangles.remove(i);
			}
		}
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
	private ArrayList<Triangle> addPoint(Vector2 point, ArrayList<Triangle> triangles) {

		// edge buffer, contains the edges that will be re-added to new
		// triangles
		ArrayList<Edge> edgeBuffer = new ArrayList<Edge>();

		// iterate over all triangles (in reverse order because the size of the
		// list is changed but not checked anymore)
		for (int j = triangles.size() - 1; j >= 0; j--) {

			Triangle T = triangles.get(j);

			// if point is inside a triangles circumcircle, add edges to
			// buffer and remove triangle

			boolean inXReach = T.inXReach(point.x); // precalculate flag boolean

			if (!T.isComplete() && inXReach) { // only do cc test if in reach
				if (T.inCC(point)) {
					edgeBuffer.add(new Edge(T.getA(), T.getB()));
					edgeBuffer.add(new Edge(T.getB(), T.getC()));
					edgeBuffer.add(new Edge(T.getC(), T.getA()));
					triangles.remove(j);
				}
			} else if (!inXReach) {
				T.flagCompleted(); // if out of reach, flag as complete
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
	private ArrayList<Edge> removeDuplicateEdges(ArrayList<Edge> edges) {

		// TODO: optimize from O(n^2) to O(n) with hash map?
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
				if (edge1.equals(edge2)) {
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

	/**
	 * This method calculates the voronoi diagram out of the given delaunay
	 * triangulation with the aid of libgdx's convex hull calculation.
	 */
	private void getVD() {

		// clear so that diagram will be constructed all new
		vDiagram.clear();

		// initialize convex hull calculator
		ConvexHull hull = new ConvexHull();

		for (Vector2 vertex : points) {

			// create point list from this list
			ArrayList<Triangle> trianglesAll = new ArrayList<Triangle>();

			// use this list for clipping later
			ArrayList<Triangle> trianglesInner = new ArrayList<Triangle>();

			// fill list with triangles that contain the vertex to determine the
			// cell
			for (Triangle triangle : dTrianglesAll) {
				if (triangle.containsPoint(vertex)) {
					trianglesAll.add(triangle);

					if (!dynamicPoints.contains(vertex)) {
						// also fill the clipping list
						if (dTriangles.contains(triangle)) {
							trianglesInner.add(triangle);
						}
					}
				}
			}

			// initialize the cell vertex array (leave space for the original
			// vertex too - just if it's an outline point)
			float vertices[];
			if (!dynamicPoints.contains(vertex)) {
				vertices = new float[trianglesAll.size() * 2 + 2];
				vertices[vertices.length - 2] = vertex.x;
				vertices[vertices.length - 1] = vertex.y;
			} else {
				vertices = new float[trianglesAll.size() * 2];
			}

			for (int i = 0; i < trianglesAll.size(); i++) {
				Triangle t = trianglesAll.get(i);
				Vector2 vPoint = new Vector2(t.getCcCenter().x, t.getCcCenter().y);
				vertices[i * 2] = vPoint.x;
				vertices[i * 2 + 1] = vPoint.y;

				// clipping the vertices outside the polygon - just for the
				// original outline points
				if (!dynamicPoints.contains(vertex) && !pointInsidePolygon(points, vPoint)) {
					float distance = 0;

					// find nearest midpoint in triangle in set
					for (int j = 0; j < trianglesInner.size(); j++) {

						Vector2[] midpoints = trianglesInner.get(j).getMidpoints();
						for (int k = 0; k < midpoints.length; k++) {
							if (distance > midpoints[k].dst(vPoint) || distance == 0) {
								distance = midpoints[k].dst(vPoint);

								// clip points to the nearest midpoint =
								// intersection point
								vertices[i * 2] = midpoints[k].x;
								vertices[i * 2 + 1] = midpoints[k].y;
							}
						}
					}
				}
			}
			vertices = hull.computePolygon(vertices, false).toArray();

			vDiagram.add(vertices);
		}
	}

	/**
	 * This method dynamically adds a new point (if the point is inside the
	 * polygon) to the existing triangulation and recalculates the voronoi
	 * diagram.
	 * 
	 * @param newP
	 *            the point to be added
	 */
	@SuppressWarnings("unchecked")
	public void dynamicUpdatePoint(Vector2 newP) {

		for (Triangle T : dTrianglesAll) {
			T.resetCompleted(); // reset the complete flags
		}

		if (pointInsidePolygon(points, newP)) {
			// add point to original point list
			points.add(newP);
			// save point in this list too to differentiate from other points
			dynamicPoints.add(newP);

			// add point incrementally to the triangulation set and update the
			// triangle list accordingly
			dTrianglesAll = addPoint(newP, dTrianglesAll);
			dTriangles = (ArrayList<Triangle>) dTrianglesAll.clone();
			for (int i = dTriangles.size() - 1; i >= 0; i--) {
				if (dTriangles.get(i).containsPoint(superT)) {
					dTriangles.remove(i);
				}
			}

			// recalculate the voronoi diagram
			getVD();
		}
	}

	/**
	 * This method dynamically adds new points (if the points are inside the
	 * polygon) to the existing triangulation and recalculates the voronoi
	 * diagram.
	 * 
	 * @param newPoints
	 *            the points to be added
	 */
	@SuppressWarnings("unchecked")
	public void dynamicUpdatePoints(Vector2[] newPoints) {

		for (Triangle T : dTrianglesAll) {
			T.resetCompleted(); // reset the complete flags
		}

		for (int i = 0; i < newPoints.length; i++) {
			if (pointInsidePolygon(points, newPoints[i])) {
				// add point to original point list
				points.add(newPoints[i]);
				// save point in this list too to differentiate from other
				// points
				dynamicPoints.add(newPoints[i]);
				// add point incrementally to the triangulation set
				dTrianglesAll = addPoint(newPoints[i], dTrianglesAll);
			}
		}

		// update the triangle list accordingly
		dTriangles = (ArrayList<Triangle>) dTrianglesAll.clone();
		for (int i = dTriangles.size() - 1; i >= 0; i--) {
			if (dTriangles.get(i).containsPoint(superT)) {
				dTriangles.remove(i);
			}
		}

		// recalculate the voronoi diagram
		getVD();
	}

	/**
	 * This is a simple point in polygon test performed with an algorithm based
	 * on the following reference that implements a Jordan scanline test:
	 * https://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
	 * 
	 * @param points
	 *            The given point set.
	 * @param testPoint
	 *            The point to be tested.
	 * @return boolean the check variable
	 */
	public boolean pointInsidePolygon(ArrayList<Vector2> points, Vector2 testPoint) {

		float[] extremes = getMinMax(points);
		float xMin = extremes[0];
		float yMin = extremes[1];
		float xMax = extremes[2];
		float yMax = extremes[3];

		// bounding box test first
		if (testPoint.x <= xMin || testPoint.x >= xMax || testPoint.y <= yMin || testPoint.y >= yMax) {
			return false;
		}

		// jordan scanline test
		boolean inside = false;
		for (int i = 0, j = points.size() - 1; i < points.size(); j = i++) {
			if ((points.get(i).y >= testPoint.y) != (points.get(j).y >= testPoint.y)
					&& testPoint.x <= (points.get(j).x - points.get(i).x) * (testPoint.y - points.get(i).y)
							/ (points.get(j).y - points.get(i).y) + points.get(i).x) {
				inside = !inside;
			}
		}

		return inside;
	}

}

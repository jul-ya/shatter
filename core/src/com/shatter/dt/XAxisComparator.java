package com.shatter.dt;

import java.util.Comparator;

import com.badlogic.gdx.math.Vector2;

/**
 * This class is a custom comparator that sorts Vector2 objects by x-axis in
 * ascending order used by the triangulator.
 * 
 * @author Julia Angerer
 * @version 1.0
 */
public class XAxisComparator implements Comparator<Vector2> {

	/**
	 * This is the overwritten compare method.
	 */
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

}

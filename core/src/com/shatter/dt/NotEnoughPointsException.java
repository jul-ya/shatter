package com.shatter.dt;

/**
 * This class is a custom exception that declares that there are not enough
 * points to start constructing a DT.
 * 
 * @author Julia Angerer
 * @version 1.0
 */
public class NotEnoughPointsException extends Exception {

	/**
	 * The serialization uid.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor for this custom exception.
	 * 
	 * @param message
	 *            The message to display.
	 */
	public NotEnoughPointsException(String message) {
		super(message);
	}
}

package it.uiip.digitalgarage.roboadvice.utils;

import java.sql.Timestamp;

/**
 * Custom class logger of the project.
 */
public class Logger {

	/**
	 * Private constructor of the class. This class must not be created. All methods are static.
	 */
	private Logger() {
	}

	/**
	 * Write the message passed as debug.
	 * 
	 * @param c
	 *            The class which call this method.
	 * @param msg
	 *            The message to print
	 */
	public static void debug(Class<?> c, String msg) {
		final Timestamp time = new Timestamp(System.currentTimeMillis());
		System.out.println("~~~ RoboAdvice ~~~ " + time + " ### " + c.getSimpleName() + ": " + msg);
	}

	/**
	 * Write the message passed in error stream.
	 * 
	 * @param c
	 *            The class which call this method.
	 * @param msg
	 *            The message to print
	 */
	public static void error(Class<?> c, String msg) {
		final Timestamp time = new Timestamp(System.currentTimeMillis());
		System.err.println("~~~ RoboAdvice ~~~ " + time + " ### " + c.getSimpleName() + ": " + msg);
	}
}

package ml.zer0dasho.plumber.utils;

import java.util.function.Consumer;

/**
 * Syntactic sugar for try/catch statements.
 * @author 0-o#9646
 */
public class Trycat {
	
	/**
	 * This is the same as a try/catch statement.
	 * Returns true if no exceptions are caught.
	 * 
	 * @param tryI - Try code encapsulated in a TryRunnable
	 * @param catchI - Catch code encapsulated in a Consumer
	 */
	public static boolean Try(TryRunnable tryI, Consumer<Exception> catchI) {
		try {
			tryI.Try();
			return true;
		} catch(Exception ex) {
			catchI.accept(ex);
			return false;
		}
	}
	
	/**
	 * Tries to get a value - otherwise, returns a default.
	 * 
	 * @param tryI
	 * @param defaultI
	 */
	public static <T> T Get(TrySupplier<T> tryI, T defaultI) {
		try {
			return tryI.Try();
		} catch(Exception ex) {
			return defaultI;
		}
	}

	public static interface TryConsumer<T> {
		public void Try(T obj) throws Exception;
	}
	
	
	public static interface TryRunnable {
		public void Try() throws Exception;
	}
	
	public static interface TrySupplier<T> {
		public T Try() throws Exception;
	}
}
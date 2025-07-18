/**
 * 
 */
package recipes.exception;

/**
 * 
 */
@SuppressWarnings("serial")
public class DbException extends RuntimeException {

	/**
	 * @param message
	 */
	public DbException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public DbException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DbException(String message, Throwable cause) {
		super(message, cause);
	}

}

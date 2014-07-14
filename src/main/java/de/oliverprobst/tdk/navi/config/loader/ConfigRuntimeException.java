package de.oliverprobst.tdk.navi.config.loader;

/**
 * A general runtime exception for the configuration component.
 * 
 * 
 */
public class ConfigRuntimeException extends RuntimeException {

	/**
	 * suid
	 */
	private static final long serialVersionUID = -4385251050252289485L;

	/**
	 * Constructor
	 * 
	 * @see RuntimeException#RuntimeException()
	 */
	public ConfigRuntimeException() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            The message
	 * @see RuntimeException#RuntimeException(String)
	 */
	public ConfigRuntimeException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param cause
	 *            The cause
	 * @see RuntimeException#RuntimeException(Throwable)
	 */
	public ConfigRuntimeException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            The message
	 * @param cause
	 *            The cause
	 * @see RuntimeException#RuntimeException(String, Throwable)
	 */
	public ConfigRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

}

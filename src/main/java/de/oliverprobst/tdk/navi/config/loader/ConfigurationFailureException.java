package de.oliverprobst.tdk.navi.config.loader;

/**
 * Will be thrown in case of an error in the configuration file.
 * 
 */
public class ConfigurationFailureException extends Exception {

	/**
	 * suid
	 */
	private static final long serialVersionUID = 7042743237477001743L;

	/**
	 * Constructor
	 * 
	 * @param message
	 *            The message of the error
	 * @param cause
	 *            The cause of the error
	 */
	public ConfigurationFailureException(String message, Throwable cause) {
		super(message, cause);
	}

}

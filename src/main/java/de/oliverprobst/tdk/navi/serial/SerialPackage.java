package de.oliverprobst.tdk.navi.serial;

/**
 * This object represents a package send via the serial connection.
 * 
 * @author <b></b>www.tief-dunkel-kalt.org</b><br>
 *         Oliver Probst <a
 *         href="mailto:oliverprobst@gmx.de">oliverprobst@gmx.de</a>
 */
public class SerialPackage {

	// private static Logger log = LoggerFactory.getLogger(SerialPackage.class);

	/** Information if the package is valid (checksum calc and size) */
	private Boolean isValid = null;

	/** The complete message including header and checksum */
	private final String message;

	/** The payload provided without any protocol overhead. */
	private final String payload;

	/** Index of the termination symbol */
	private int terminatorIndex = 0;

	/**
	 * Instantiates a new serial package.
	 *
	 * @param message
	 *            the message to parse.
	 */
	public SerialPackage(String message) {

		this.message = message;
		terminatorIndex = message.indexOf('*');

		if (message.length() > 2 && terminatorIndex != -1) {
			this.payload = message.substring(2, message.indexOf('*'));
		} else {
			this.isValid = false;
			this.payload = null;
		}
	}

	/**
	 * Calculate the checksum for this payload. This calculation excludes the
	 * '$', but includes '*'.
	 *
	 * @return the calculated check sum
	 */
	public int getCalculatedCheckSum() {
		if (message.length() < 3) {
			return -1;
		}
		int pos = 1;
		int checksum_A = 0;
		int checksum_B = 0;

		do {
			int nextByte = message.charAt(pos);
			checksum_A = (checksum_A + nextByte) & 0xff;
			checksum_B = (checksum_B + checksum_A)  & 0xff;

		} while (pos++ < terminatorIndex); // includes terminator

		return checksum_A * 10000 + checksum_B;
	}

	/**
	 * Gets the payload provided without any protocol overhead.
	 *
	 * @return the payload provided without any protocol overhead
	 */
	public String getPayload() {
		return payload;
	}

	/**
	 * Parses and returns the checksum in the message as integer.
	 *
	 * @return the received checksum
	 */
	public int getReceivedChecksum() {
		if (message.length() < 3) {
			return -1;
		}
		int checksum_A = message.charAt(terminatorIndex + 1);
		int checksum_B = message.charAt(terminatorIndex + 2);

		return checksum_A * 10000 + checksum_B;

	}

	/**
	 * Gets the type of the message (eg. course, nmea string, leak, etc.)
	 *
	 * @return the type
	 */
	public MessageType getType() {
		return MessageType.getMessageType((byte) message.charAt(1));
	}

	/**
	 * Checks if the message is valid.
	 *
	 * @return true, if is valid
	 */
	public boolean isValid() {
		if (isValid == null) {
			isValid = terminatorIndex > 0
					&& getCalculatedCheckSum() == getReceivedChecksum();
		}
		return isValid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SerialPackage [msg=" + payload + "]";
	}
}
